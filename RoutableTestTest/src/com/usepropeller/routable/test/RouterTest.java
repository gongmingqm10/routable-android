package com.usepropeller.routable.test;

import java.net.URLEncoder;
import java.util.Map;

import com.usepropeller.routable.Router;

import junit.framework.Assert;

import android.app.ListActivity;
import android.content.Intent;
import android.test.AndroidTestCase;


public class RouterTest extends AndroidTestCase {
	private boolean _called;
    @Override
	public void setUp() throws Exception {
		super.setUp();
        Router.init(mContext);
		this._called = false;
	}

	public void test_basic() {
		Router.sharedRouter().map("users/:user_id", ListActivity.class);
		Intent intent = Router.sharedRouter().intentFor("users/4");
		Assert.assertEquals("4", intent.getExtras().getString("user_id"));
	}

	public void test_empty() {
		Router.sharedRouter().map("users", ListActivity.class);
		Intent intent = Router.sharedRouter().intentFor("users");
		Assert.assertNull(intent.getExtras());
	}

	public void test_invalid_route() {
		boolean exceptionThrown = false;

		try {
			Router.sharedRouter().intentFor("ming/4");
		} catch (Router.RouteNotFoundException e) {
			exceptionThrown = true;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Incorrect exception throw: " + e.toString());
		}

		Assert.assertTrue("Invalid route did not throw exception", exceptionThrown);
	}

	public void test_code_callbacks() {
		Router.sharedRouter().map("callback", ListActivity.class, new Router.RouterCallback() {
            @Override
            public void run(Map<String, String> params) {
                RouterTest.this._called = true;
            }
        });

		Router.sharedRouter().open("callback");

		Assert.assertTrue(this._called);
	}

	public void test_code_callbacks_with_params() {
		Router.sharedRouter().map("callback/:id", ListActivity.class, new Router.RouterCallback() {
            @Override
            public void run(Map<String, String> params) {
                RouterTest.this._called = true;
                Assert.assertEquals("123", params.get("id"));
            }
        });

        Router.sharedRouter().open("callback/123");

		Assert.assertTrue(this._called);
	}

    public void test_complex_params() {
        Router.sharedRouter().map("web/:url/:title", ListActivity.class);
        Intent intent = Router.sharedRouter().intentFor(
                String.format("web/%s/%s", Router.safeEncode("http://google.com"), Router.safeEncode("Google"))
        );
        Assert.assertEquals("Intent should get correct URL", intent.getStringExtra("url"), "http://google.com");
        Assert.assertEquals("Title should also be correct", intent.getStringExtra("title"), "Google");
    }

}
