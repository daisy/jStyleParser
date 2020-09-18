package cz.vutbr.web.csskit.antlr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import cz.vutbr.web.css.NetworkProcessor;
import cz.vutbr.web.csskit.DefaultNetworkProcessor;

/**
 * CSSSourceReader driven by a NetworkProcessor that can handle media type text/css.
 *
 * @author bertfrees
 */
public class DefaultCSSSourceReader implements CSSSourceReader {

	private final NetworkProcessor network;

	public DefaultCSSSourceReader() {
		this(new DefaultNetworkProcessor());
	}

	public DefaultCSSSourceReader(NetworkProcessor network) {
		this.network = network;
	}

	public CSSInputStream read(CSSSource source) throws IOException {
		switch (source.type) {
		case INLINE:
		case EMBEDDED:
			return new CSSInputStream(
				new ByteArrayInputStream(((String)source.source).getBytes()),
				source.encoding);
		case URL:
			return new CSSInputStream(
				network.fetch((URL)source.source),
				source.encoding);
		default:
			throw new RuntimeException("coding error");
		}
	}
}
