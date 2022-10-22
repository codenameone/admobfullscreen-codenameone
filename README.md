# admobfullscreen-codenameone

A Codename One library project that uses Admob Interstitial Ads 
to display full screen ads - https://developers.google.com/mobile-ads-sdk/docs/admob/android/interstitial

The library is implemented for Android, iOS and the Simulator

Thanks for Ram for contributing.(the creator of yhomework)

# Sample Code
```java
public class MyApplication {

    private Form current;

    private AdMobManager admob;
    
    public void init(Object context) {
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch(IOException e){
            e.printStackTrace();
        }
        
        String admobId = "<your android adID>";
        if(Display.getInstance().getPlatformName().equals("ios")){
            admobId = "<your iphone adID>";
        }
        admob = new AdMobManager(admobId);
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        
        Form f = new Form("FullScreen Ads");
        f.addComponent(new Button(new Command("start"){

            public void actionPerformed(ActionEvent evt) {
                admob.loadAd();
            }
        
        }));
        
        f.addComponent(new Button(new Command("show"){

            public void actionPerformed(ActionEvent evt) {
                admob.showAdIfLoaded();
            }
        
        }));

        f.addComponent(new Button(new Command("load & show"){

            public void actionPerformed(ActionEvent evt) {
                admob.loadAndShow();
            }
        
        }));

        f.show();
        
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }

}
```

# Details

## Adding to Maven App Projects

Add dependency to your common/pom.xml file:

```xml
<dependency>
    <groupId>com.codenameone</groupId>
    <artifactId>admob-fullscreen-lib</artifactId>
    <version>LATEST</version>
    <type>pom</type>
</dependency>
```

## Adding to Ant App Projects

Add the "admob-fullscreen" plugin through Codename One extensions (in Codename One Settings)


## Build from Source

Requirements:

1. JDK 1.8 or 11

Steps:

1. Clone the repo
2. `mvn install`

This will install the lib into your local maven repo.  If you want to use this in your maven project, you may need to adjust the `<version>` in your app project's dependency to match the `<version>` in the library pom.xml file.

This will also build a cn1lib in the common/target directory that you can install into Ant app projects by copying it to the app project's cn1libs directory, and running "Refresh CN1libs"