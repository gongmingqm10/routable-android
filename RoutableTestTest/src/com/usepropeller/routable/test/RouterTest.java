package com.usepropeller.routable.test;

import android.os.Bundle;
import com.usepropeller.routable.Router;

import junit.framework.Assert;

import android.app.ListActivity;
import android.content.Intent;
import android.test.AndroidTestCase;


public class RouterTest extends AndroidTestCase {

    @Override
	public void setUp() throws Exception {
		super.setUp();
        Router.init(mContext);
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

    public void test_special_params() {
        Router.sharedRouter().map("web/:url/:title", ListActivity.class);
        Intent intent = Router.sharedRouter().intentFor(
                String.format("web/%s/%s", Router.safeEncode("http://google.com"), Router.safeEncode("Google"))
        );
        Assert.assertEquals("Intent should get correct URL", intent.getStringExtra("url"), "http://google.com");
        Assert.assertEquals("Title should also be correct", intent.getStringExtra("title"), "Google");
    }

    public void test_bundle_params() {
        Router.sharedRouter().map("player", ListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("source", "youtube");
        bundle.putInt("type", 100);
        bundle.putBoolean("flag", true);
        Intent intent = Router.sharedRouter().intentFor("player", bundle);
        Bundle realBundle = intent.getExtras();
        Assert.assertEquals(realBundle.get("source"), "youtube");
        Assert.assertEquals(realBundle.get("type"), 100);
        Assert.assertEquals(realBundle.get("flag"), true);
    }

}
