# Routable

Routable is an in-app native URL router for Android. Forked from (https://github.com/clayallsopp/routable-android).

In this fork, we mainly add the support for special parameters and refactor some code.

## Usage

Set up your app's router and URLs:

```java
import com.usepropeller.routable.Router;

public class PropellerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.sharedRouter().init(getApplicationContext());
        // Symbol-esque params are passed as intent extras to the activities, state all basic map rules.
        Router.sharedRouter().map("users/:id", UserActivity.class);
        Router.sharedRouter().map("users/new/:name/:zip", NewUserActivity.class);
        Router.sharedRouter().map("web/:url/:title", WebViewActivity.class);
    }
}
```

*Anywhere* else in your app, open some URLs:

```java
// starts a new UserActivity
Router.sharedRouter().open("users/16");
// starts a new NewUserActivity
Router.sharedRouter().open("users/new/Clay/94303");

```

In your `Activity` classes, add support for the URL params:

```java
import com.usepropeller.routable.Router;

public class UserActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intentExtras = getIntent().getExtras();
        // We will get the userId = 2
        String userId = intentExtras.get("id");
        //The same effect with: String userId = intent.getStringExtra("id");
    }
}

public class NewUserActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intentExtras = getIntent().getExtras();
        // Corresponds to the ":name" above
        String name = intentExtras.get("name");
        // Corresponds to the ":zip" above
        String zip = intentExtras.get("zip");
    }
}
```

If you want to add Bundle params to the Activity you want to open.

Simple use as follows,

```java

Bundle bundle = new Bundle();
bundle.putString("name", "gongmingqm10");
bundle.putInt("star", 100);
Intent intent = Router.sharedRouter().intentFor("users/16", bundle);

```

Then in the `UserActivity` class, we can get id, name and star with the following code.

```java
public class UserActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intentExtras = getIntent().getExtras();
        String userId = intentExtras.get("id"); // userId = "16"
        int userStar = intentExtras.get("star"); //star = 100
        String name = intentExtras.get("name"); //name = "gongmingqm10"
    }
}

```

More special, if we want to put a direct url params into our router map, how can it compile with `/`

We can use Router.safeEncode() to encode some special params to avoid router mistake. Of course, we can put what we want to Bundle

```java
Intent intent = Router.sharedRouter().intentFor(
        String.format("web/%s/%s", Router.safeEncode("http://google.com"), Router.safeEncode("Google"))
);

```

Then in the `WebViewActivity`, we can get the url and title params.

```java
public class WebViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = intent.getStringExtra("url"); // url = "http://google.com"
        String title = intent.getStringExtra("title"); // title = "Google"
    }
}

```

## Installation

Routable is currently an Android library project (so no Maven).

If you're in a hurry, you can just copy-paste the [Router.java](https://github.com/gongmingqm10/routable-android/blob/master/src/com/usepropeller/routable/Router.java) file.

Or if you're being a little more proactive, you should import the Routable project (this entire git repo) into Eclipse and [reference it](http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject) in your own project. 

## Features

### Routable Functions

### Open External URLs

Sometimes you want to open a URL outside of your app, like a YouTube URL or open a web URL in the browser. You can use Routable to do that:

```java
Router.sharedRouter().openExternal("http://www.youtube.com/watch?v=oHg5SJYRHA0")
```

## Contact

Gong Ming ([http://www.gongmingqm10.net](http://www.gongmingqm10.net))

- [http://github.com/gongmingqm10](http://github.com/gongmingqm10)
- [gongmingqm10@gmail.com](gongmingqm10@gmail.com)

