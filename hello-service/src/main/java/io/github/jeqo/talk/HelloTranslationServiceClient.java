package io.github.jeqo.talk;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

class HelloTranslationServiceClient {

	private final HttpClient httpClient;

	private final String url;

	HelloTranslationServiceClient(HttpClient httpClient, String url) {
		this.httpClient = httpClient;
		this.url = url;
	}

	String translateHello(final String lang) throws IOException {
		final HttpResponse response = httpClient.execute(new HttpGet(url + "/" + lang));
		return EntityUtils.toString(response.getEntity());
	}

}
