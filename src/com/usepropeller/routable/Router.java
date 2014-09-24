/*
    Routable for Android
    Copyright (c) 2013 Turboprop, Inc. <clay@usepropeller.com>
    http://usepropeller.com

    Licensed under the MIT License.

    Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

package com.usepropeller.routable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Router {

	private static Router instance;
    private String rootUrl;

	public static Router sharedRouter() {
        if (instance == null) instance = new Router();
		return instance;
	}

	private class RouterParams {
		private  Map<String, String> openParams;
        private Class<? extends Activity> klass;

        public RouterParams(Class<? extends Activity> klass) {
            this.klass = klass;
        }

        private RouterParams(Class<? extends Activity> klass, Map<String, String> openParams) {
            this.openParams = openParams;
            this.klass = klass;
        }

        public void setOpenParams(Map<String, String> openParams) {
            this.openParams = openParams;
        }

        public Map<String, String> getOpenParams() {
            return openParams;
        }

        public Class<? extends Activity> getKlass() {
            return klass;
        }
    }

	private final Map<String, RouterParams> routers = new HashMap<String, RouterParams>();
	private final Map<String, RouterParams> cachedRoutes = new HashMap<String, RouterParams>();
	private static Context mContext;

	private Router() {
        if (mContext == null)
            throw new ContextNotProvided();
	}

    public static void init(Context context) {
        mContext = context;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * Map a URL to open an {@link Activity}
     * @param format The URL being mapped; for example, "users/:id" or "groups/:id/topics/:topic_id"
     * @param klass The {@link Activity} class to be opened with the URL
     */
	public void map(String format, Class<? extends Activity> klass) {
		this.map(format, new RouterParams(klass));
	}

	/**
     * Map a URL to open an {@link Activity}
     * @param format The URL being mapped; for example, "users/:id" or "groups/:id/topics/:topic_id"
     */
	public void map(String format, RouterParams params) {
		if (params == null) {
            throw new RouteNotFoundException("There is no router found in Router");
        }
		routers.put(format, params);
	}

	/**
     * Open a URL using the operating system's configuration (such as opening a link to Chrome or a video to YouTube)
     * @param url The URL; for example, "http://www.youtube.com/watch?v=oHg5SJYRHA0"
     */
	public void openExternal(String url) {
		this.openExternal(url, this.mContext);
	}

	/**
     * Open a URL using the operating system's configuration (such as opening a link to Chrome or a video to YouTube)
     * @param url The URL; for example, "http://www.youtube.com/watch?v=oHg5SJYRHA0"
     * @param context The context which is used in the generated {@link Intent}
     */
	public void openExternal(String url, Context context) {
		this.openExternal(url, null, context);
	}

	/**
     * Open a URL using the operating system's configuration (such as opening a link to Chrome or a video to YouTube)
     * @param url The URL; for example, "http://www.youtube.com/watch?v=oHg5SJYRHA0"
     * @param extras The {@link Bundle} which contains the extras to be assigned to the generated {@link Intent}
     */
	public void openExternal(String url, Bundle extras) {
		this.openExternal(url, extras, this.mContext);
	}

	/**
     * Open a URL using the operating system's configuration (such as opening a link to Chrome or a video to YouTube)
     * @param url The URL; for example, "http://www.youtube.com/watch?v=oHg5SJYRHA0"
     * @param extras The {@link Bundle} which contains the extras to be assigned to the generated {@link Intent}
     * @param context The context which is used in the generated {@link Intent}
     */
	private void openExternal(String url, Bundle extras, Context context) {
		if (context == null) {
			throw new ContextNotProvided();
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		if (extras != null) intent.putExtras(extras);
		context.startActivity(intent);
	}

	/**
     * Open a map'd URL set using {@link #map(String, Class)}
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
     */
	public void open(String url) {
		this.open(url, this.mContext);
	}

	/**
     * Open a map'd URL set using {@link #map(String, Class)}
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
     * @param extras The {@link Bundle} which contains the extras to be assigned to the generated {@link Intent}
     */
	public void open(String url, Bundle extras) {
		this.open(url, extras, this.mContext);
	}

	/**
     * Open a map'd URL set using {@link #map(String, Class)}
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
     * @param context The context which is used in the generated {@link Intent}
     */
	public void open(String url, Context context) {
		this.open(url, null, context);
	}

	/**
     * Open a map'd URL set using {@link #map(String, Class)}
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
     * @param extras The {@link Bundle} which contains the extras to be assigned to the generated {@link Intent}
     * @param context The context which is used in the generated {@link Intent}
     */
	public void open(String url, Bundle extras, Context context) {
		if (context == null) {
            throw new ContextNotProvided();
        }
		Intent intent = this.intentFor(context, url);
		if (extras != null) intent.putExtras(extras);
		context.startActivity(intent);
	}

	/**
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
	 * @return The {@link Intent} for the url
	 */
	public Intent intentFor(String url) {
		return intentFor(mContext, url);
	}
	/**
	 *
	 * @param context The context which is spawning the intent
     * @param url The URL; for example, "users/16" or "groups/5/topics/20"
	 * @return The {@link Intent} for the url, with the correct {@link Activity} set, or null.
	 */
	public Intent intentFor(Context context, String url) {
		RouterParams params = this.paramsForUrl(url);
        Intent intent = new Intent();
        for (Entry<String, String> entry : params.openParams.entrySet()) {
            intent.putExtra(entry.getKey(), safeDecode(entry.getValue()));
        }
		intent.setClass(context, params.getKlass());
		return intent;
	}

	private RouterParams paramsForUrl(String url) {
		if (this.cachedRoutes.get(url) != null) {
			return this.cachedRoutes.get(url);
		}
		String[] realSegments = url.split("/");
		RouterParams openParams = null;
		for (Entry<String, RouterParams> entry : this.routers.entrySet()) {
			String routerUrl = entry.getKey();
			RouterParams routerParams = entry.getValue();
			String[] routerSegments = routerUrl.split("/");
			if (routerSegments.length != realSegments.length) continue;
			Map<String, String> givenParams = urlToParamsMap(realSegments, routerSegments);
			if (givenParams == null) continue;
            openParams = new RouterParams(routerParams.getKlass(), givenParams);
			break;
		}
		if (openParams == null) throw new RouteNotFoundException("No route found for url " + url);
		this.cachedRoutes.put(url, openParams);
		return openParams;
	}

	private Map<String, String> urlToParamsMap(String[] realSegments, String[] routerSegments) {
		Map<String, String> formatParams = new HashMap<String, String>();
		for (int index = 0; index < routerSegments.length; index++) {
			String routerPart = routerSegments[index];
			String givenPart = realSegments[index];
			if (routerPart.charAt(0) == ':') {
				String key = routerPart.substring(1, routerPart.length());
				formatParams.put(key, givenPart);
				continue;
			}
			if (!routerPart.equals(givenPart)) {
				return null;
			}
		}
		return formatParams;
	}

	public static class RouteNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
        private static final String NO_ROUTE_MESSAGE = "No route found for url";

        public RouteNotFoundException() {
            super(NO_ROUTE_MESSAGE);
        }

		public RouteNotFoundException(String message) {
			super(message);
		}
	}

	public static class ContextNotProvided extends RuntimeException {
		private static final long serialVersionUID = 1L;
        private static final String NO_CONTEXT_MESSAGE = "No context found for router";
        public ContextNotProvided() {
            super(NO_CONTEXT_MESSAGE);
        }
	}

    public static String safeEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String safeDecode(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
