package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v123.network.model.RequestId;
import org.openqa.selenium.devtools.v123.network.Network;
import org.openqa.selenium.devtools.v123.network.model.ResponseReceived;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.*;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.Optional;
import java.util.function.Consumer;

import static org.openqa.selenium.devtools.v123.network.Network.setCacheDisabled;
import static org.testng.Assert.*;

public class automationTest {

    public String root="";
    public static int count=0;
    public String ID="";
    public String s;
    public JSONObject jsonObject;
    public JSONArray jsonArray;
    public String url;
    public ChromeDriver driver;
    //public WebDriver driver;
    public DevTools devTool;
    public ChromeOptions options=new ChromeOptions();

    public Properties prop=new Properties() ;
    public String browser;
    public String responseBody;
    private final String propertyFilePath= "/config/configuration.properties";
    public  FileReader reader;
    // declare BrowserStack credentails as environment variables
    //  public static final String USERNAME = (System.getenv("BROWSERSTACK_USERNAME") != null) ? System.getenv("BROWSERSTACK_USERNAME") : "kinchitjain_r0AfPS";
    // public static final String AUTOMATE_KEY = (System.getenv("BROWSERSTACK_ACCESS_KEY") != null) ? System.getenv("BROWSERSTACK_ACCESS_KEY") : "ayzNLR1Vq1XMpSxzXPU5";
    // declare remote URL in a variable
    // public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";
    // intialize Selenium WebDriver
    MutableCapabilities capabilities = new MutableCapabilities();


    private Map<String, Object> getChromePrefsNoJS() {
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.managed_default_content_settings.javascript", 2);
        chromePrefs.put("profile.managed_default_content_settings.cookies", 1);
        return chromePrefs;
    }

    private Map<String, Object> getChromePrefsNoCookie() {
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.managed_default_content_settings.cookies", 2);
        chromePrefs.put("profile.managed_default_content_settings.javascript", 1);
        return chromePrefs;
    }

    private Map<String, Object> getChromePrefsNoCookieNoJS() {
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.managed_default_content_settings.cookies", 2);
        chromePrefs.put("profile.managed_default_content_settings.javascript", 2);
        return chromePrefs;
    }
    public String getApplicationUrl() {
        url = prop.getProperty("ic.url");
        System.out.println("URL: "+url);
        if(url != null) return url;
        else throw new RuntimeException("url not specified in the configuration.properties file.");
    }


    @Test
    public void testWsa() throws InterruptedException, IOException {
        setup();

        File file =new File("id.txt");
        FileReader r=new FileReader(file);

        BufferedReader reader = new BufferedReader(r);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        ID = stringBuilder.toString();


        driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();//delete all cookies

        driver.get("http://control.cloud-sqa-shared.akamai.com/apps/auth/#/login");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//input[@name='username']")).sendKeys(prop.getProperty("ic.username"));
        Thread.sleep(2000);
        driver.findElement(By.name("next-btn")).click();
        Thread.sleep(2000);
        //driver.get(this.password).should('be.visible');
        driver.findElement((By.xpath("//akam-text-field[@id='auth-username']/input"))).sendKeys(prop.getProperty("ic.password"));
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@name='sign-in-btn']")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("//input[@placeholder='Search']")).sendKeys(prop.getProperty("ic.account"));
        Thread.sleep(2000);
        new Actions(driver)
                .keyDown(Keys.ENTER)
                .perform();
        Thread.sleep(2000);
        driver.findElement(By.linkText(prop.getProperty("ic.account"))).click();
        Thread.sleep(5000);



        devTool = driver.getDevTools();
        devTool.createSession();
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),

                Optional.empty()));
        final RequestId[] requestIds = new RequestId[1];

        devTool.addListener(Network.responseReceived(), response -> {

            requestIds[0] = response.getRequestId();

            url=response.getResponse().getUrl();
            //System.out.println("url String "+url);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(url.contains("/web-security-analytics-be/v2/sampleData?source=WSA2.0&t=2")) {
                //System.out.println("Before String ");
                responseBody = devTool.send(Network.getResponseBody(requestIds[0])).getBody();
                // System.out.println("Extract String "+responseBody);
                //String stringToParse = response.getResponse().toString();
                //System.out.println("Extract String "+responseBody);
                Gson gson=new Gson();

                //JsonReader jr=gson.fromJson(responseBody,String.class));

                JsonObject jo= JsonParser.parseString(responseBody).getAsJsonObject();
                for(int i=0;i<jo.getAsJsonArray("data").size();i++) {

                    JsonObject responseJS = (JsonObject) jo.getAsJsonArray("data").get(i);


                    JsonArray headers = responseJS.getAsJsonArray("response_headers");
                    //JsonObject jo=jsonArray.getAsJsonObject();
                    // System.out.println(headers);


                    int flag = 0;

                    for (int j = 0; j < headers.size(); j++) {
                        JsonObject json = headers.get(j).getAsJsonObject();
                        // System.out.println(json);
                        if (json.get("name").getAsString().equals("Akamai-X-Request-ID")) {

                            if (json.get("value").getAsString().equals(ID)) {
                                flag = 1;
                                System.out.println("*****");


                            }
                        }

                    }

                    if (flag == 1) {
                        for (int i1 = 0; i1 < headers.size(); i1++) {
                            JsonObject json = headers.get(i1).getAsJsonObject();
                            System.out.println(json);
                        }

                    }
                }

            }

        });

        driver.get("https://control.cloud-sqa-shared.akamai.com/apps/securitycenter/#/ng-web-security-analytics");

        new WebDriverWait(driver,Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='aci-caret-bottom']"))).click();
        Thread.sleep(7000);
        driver.findElement(By.xpath("//i[@class='aci-search']/../input")).sendKeys(prop.getProperty("ic.config"));

        new WebDriverWait(driver,Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='item-container']/div/div/span/span/span"))).click();

        //driver.findElement(By.xpath("//input[@id='add-dimension-search']")).sendKeys("BOT MANAGEMENT");
        //Thread.sleep(5000);
        //driver.findElement(By.xpath("//div[@id='add-dimension-bot-ruleid']/span")).click();
        Thread.sleep(5000);



        new WebDriverWait(driver,Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Samples')]"))).click();
        Thread.sleep(5000);


    }




    @BeforeSuite
    public void setup() {

        try {
            root= System.getProperty("user.dir");
            System.out.println("Root ########:"+root);
            reader= new FileReader(root+propertyFilePath);
            prop.load(reader);
            if(prop.getProperty("ic.browser").toString().equalsIgnoreCase("Chrome")) {
                browser=prop.getProperty("ic.browser");
                options = new ChromeOptions();
                //options.addArguments("--headless=new");
            }
            // login(prop.getProperty("ic.username"),prop.getProperty("ic.password"));



        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("configuration not found at " + propertyFilePath);
        }


    }

    @BeforeMethod
    public void testMethod(Method method)
    {   if (method.getName().toString().equals("testICReferer")) {
        url = prop.getProperty("ic.url.referer");
        //url="https://qa.themaha.in/ic/";
    }

    }

    @Test(groups = {"IC"})
    public void testICPositive() throws IOException, InterruptedException {
        System.out.println("IC POSITIVE *********");



        driver = new ChromeDriver();
        devTool = driver.getDevTools();
        devTool.createSession();
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),

                Optional.empty()));


        devTool.addListener(Network.responseReceived(), response -> {



            String url = response.getResponse().getUrl();
            if(url.equals(s)) {
                if(count==0) {
                    System.out.println("Initiate Call Happy Path ");
                    count++;
                }
                if(count==1)
                {
                    System.out.println("Followup Call Happy Path");
                    count++;
                }
            }

            if(url.contains("_sec/verify?provider=interstitial"))
            {
                System.out.println("Delivery call Happy Path");
            }

            String headers = response.getResponse().getHeaders().toString();
            //System.out.println("Happy Path headers => " + headers);




        });
        System.out.println("Application url"+getApplicationUrl());
        s= getApplicationUrl();

        driver.get(s.trim());

        new WebDriverWait(driver,Duration.ofSeconds(30)).until(ExpectedConditions.textToBe(By.className("lead"),"This is a challenge framework testing app."));
        Cookie cookie = driver.manage().getCookieNamed("ak_bmsc");
        Assert.assertNotNull(cookie, "ak_bmsc not present happy path");



    }



    @Test(groups = {"cookie"})
    public void testICNoCookie() throws IOException, InterruptedException {
        System.out.println("IC NO COOKIE *********");
        options.setExperimentalOption("prefs", getChromePrefsNoCookie());

        driver = new ChromeDriver(options);
        devTool = driver.getDevTools();
        devTool.createSession();
        List<String> headerList = new ArrayList<String>();
        devTool.send(setCacheDisabled(true));
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),

                Optional.empty()));
        final RequestId[] requestIds = new RequestId[1];
        Consumer<ResponseReceived> responseReceivedConsumer = responseReceived -> {

            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();
            // System.out.println("url => " + url);

            int status = responseReceived.getResponse().getStatus();
            String headers = responseReceived.getResponse().getHeaders().toString();
            headerList.add(headers);
            // System.out.println("status => " + status);

            if (status == 200 && url.equals(s)) {
                System.out.println("Initiate Call No Cookie");
            }
            if (status == 400 && url.contains("_sec/verify?provider=interstitial")) {
                System.out.println("Delivery Call No Cookie");
            }
            if (status == 403 && url.contains("bm-verify=")) {
                System.out.println(headers);

                String [] headerArray=headers.split(",");
                if(headerArray[3].contains("Akamai-X-Request-ID")) {
                    String[] requestID = headerArray[3].split("=");
                    System.out.println("requestID " + requestID[1]);
                    ID = requestID[1];
                }
                FileWriter fstream;

                try {
                    fstream = new FileWriter("id.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(ID);
                    out.newLine();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                System.out.println("Followup Call No Cookie");
                assertTrue(driver.getCurrentUrl().contains("bm-verify="));



                Iterator i = headerList.iterator();
                while (i.hasNext()) {
                    String temp = i.next().toString();
                    if (temp.contains("Akamai-X-WAF-Alerted-Rules=3912002")) {
                        System.out.println("Akamai-X-WAF-Alerted-Rules=3912002  Delivery call No JS");
                    }
                    if (temp.contains("Akamai-X-WAF-Triggered-Rules\n" +
                            "\t=3912002:BOT-INTERSTITIAL-CHALLENGE")) {
                        System.out.println("Akamai-X-WAF-Triggered-Rules\n" +
                                "\t=3912001:BOT-INTERSTITIAL-CHALLENGE   Delivery call No JS");
                    }
                }
            }


            System.out.println("-------------------------------------------------");


        };
        devTool.addListener(Network.responseReceived(), responseReceivedConsumer);

        System.out.println("Application url"+getApplicationUrl());
        s= getApplicationUrl();
        driver.get(s.trim());
        Thread.sleep(5000);
        new WebDriverWait(driver,Duration.ofSeconds(30)).until(ExpectedConditions.titleIs("Access Denied"));
        Cookie cookie = driver.manage().getCookieNamed("ak_bmsc");
        Assert.assertNull(cookie, "ak_bmsc present No cookie scenario");





    }


    @Test(groups = {"referer"},enabled = true)
    public void testICReferer() throws IOException, InterruptedException {
        System.out.println("IC REFERER *********");
        driver = new ChromeDriver();
        devTool = driver.getDevTools();
        devTool.createSession();
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),
                Optional.empty()));

        devTool.addListener(Network.requestWillBeSent(), requestSent -> {
            if (requestSent.getRequest().getUrl().equals(s)) {
                if (requestSent.getRequest().getHeaders().containsKey("Referer")) {
                    System.out.println("-----------inside Referer------------------");
                    String url = requestSent.getRequest().getUrl();
                    System.out.println("url => " + url);
                    System.out.println("Referer: " + requestSent.getRequest().getHeaders().get("Referer").toString());
                    assertEquals(requestSent.getRequest().getHeaders().get("Referer").toString(), s);
                }
            }

        });

        s=url;
        driver.get(s.trim());
        Thread.sleep(5000);
        driver.findElement(By.xpath("//a[contains(text(),'qa.themaha.in')]")).click();


        new WebDriverWait(driver,Duration.ofSeconds(20)).until(ExpectedConditions.textToBe(By.xpath("//a[contains(text(),'Unprotected')]"),"Unprotected"));


    }



    @Test(groups = {"js"},enabled = true)
    public void testICNoJS() throws IOException, InterruptedException {
        System.out.println("IC NO JS *********");

        options.setExperimentalOption("prefs", getChromePrefsNoJS());

        driver=new ChromeDriver(options);

        // driver = new RemoteWebDriver(new URL(URL), options);
        //driver = new Augmenter().augment(driver);


        devTool = driver.getDevTools();
        devTool.createSession();
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),

                Optional.empty()));
        //final RequestId[] requestIds = new RequestId[1];
        List<String> headerList = new ArrayList<String>();
        devTool.addListener(Network.responseReceived(), responseReceived -> {



            Integer status = responseReceived.getResponse().getStatus();

            String headers = responseReceived.getResponse().getHeaders().toString();
            headerList.add(headers);

            if(status.equals(200)&&driver.getCurrentUrl().equals(s)) {
                // Assert.assertTrue(driver.getCurrentUrl().contains("ch.php"));
                //Iterator i=headerList.iterator();

                if(headers.contains("Akamai-X-WAF-Alerted-Rules=3900999")) {
                    System.out.println("Akamai-X-WAF-Alerted-Rules=3900999  Initiate call No JS");
                }



            }
            if(status.equals(403)) {

                System.out.println(headers);
                String [] headerArray=headers.split(",");
                String [] requestID=headerArray[3].split("=");
                System.out.println("requestID "+ requestID[1]);
                ID=requestID[1];
                FileWriter fstream;

                try {
                    fstream = new FileWriter("id.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(ID);
                    out.newLine();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Assert.assertTrue(driver.getCurrentUrl().contains("bm-verify="));
                // System.out.println(headers);
                // String [] headerArray=headers.split(",");
                // String [] requestID=headerArray[2].split("=");
                // System.out.println("requestID "+ requestID[1]);
                ID=requestID[1];
                Iterator i=headerList.iterator();
                while(i.hasNext()) {
                    String temp=i.next().toString();
                    if(temp.contains("Akamai-X-WAF-Alerted-Rules=3912001")) {
                        System.out.println("Akamai-X-WAF-Alerted-Rules=3912001  Delivery call No JS");
                    }
                    if(temp.contains("Akamai-X-WAF-Triggered-Rules=3912001:BOT-INTERSTITIAL-CHALLENGE"))
                    {
                        System.out.println("Akamai-X-WAF-Triggered-Rules=3912001:BOT-INTERSTITIAL-CHALLENGE   Delivery call No JS");
                    }
                }


            }


            System.out.println("-------------------------------------------------");


        });



        System.out.println("Application url"+getApplicationUrl());
        s= getApplicationUrl();

        driver.get(s);
        Thread.sleep(5000);
        Cookie cookie = driver.manage().getCookieNamed("ak_bmsc");
        Assert.assertNotNull(cookie,"ak_bmsc cookie is null");

        new WebDriverWait(driver,Duration.ofSeconds(30)).until(ExpectedConditions.titleIs("Access Denied"));

        //wsa();



    }

    @Test(groups = {"both"},enabled = true)
    public void testICNoCookieNoJS() throws IOException, InterruptedException {
        System.out.println("IC NO COOKIE NO JS *********");

        options.setExperimentalOption("prefs", getChromePrefsNoCookieNoJS());



        driver=new ChromeDriver(options);

        // driver = new RemoteWebDriver(new URL(URL), options);
        //driver = new Augmenter().augment(driver);


        devTool = driver.getDevTools();
        devTool.createSession();
        devTool.send(Network.enable(Optional.empty(), Optional.empty(),

                Optional.empty()));
        //final RequestId[] requestIds = new RequestId[1];
        List<String> headerList = new ArrayList<String>();
        devTool.addListener(Network.responseReceived(), responseReceived -> {



            Integer status = responseReceived.getResponse().getStatus();

            String headers = responseReceived.getResponse().getHeaders().toString();
            headerList.add(headers);

            if(status.equals(200)&&driver.getCurrentUrl().equals(s)) {
                // Assert.assertTrue(driver.getCurrentUrl().contains("ch.php"));
                //Iterator i=headerList.iterator();

                if(headers.contains("Akamai-X-WAF-Alerted-Rules=3900999")) {
                    System.out.println("Akamai-X-WAF-Alerted-Rules=3900999  Initiate call No Cookie No JS");
                }



            }
            if(status.equals(403)) {

                System.out.println(headers);
                String [] headerArray=headers.split(",");
                String [] requestID=headerArray[2].split("=");
                System.out.println("requestID "+ requestID[1]);
                ID=requestID[1];
                FileWriter fstream;

                try {
                    fstream = new FileWriter("id.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(ID);
                    out.newLine();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println(headers);
                //String [] headerArray=headers.split(",");
                //String [] requestID=headerArray[2].split("=");
                //System.out.println("requestID "+ requestID[1]);
                //ID=requestID[1];
                Assert.assertTrue(driver.getCurrentUrl().contains("bm-verify="));
                Iterator i=headerList.iterator();
                while(i.hasNext()) {
                    String temp=i.next().toString();
                    if(temp.contains("Akamai-X-WAF-Alerted-Rules=3912001:3912002")) {
                        System.out.println("Akamai-X-WAF-Alerted-Rules=3912001:3912002  Delivery call No Cookie No JS");
                    }
                    if(temp.contains("Akamai-X-WAF-Triggered-Rules=3912001:3912002:BOT-INTERSTITIAL-CHALLENGE"))
                    {
                        System.out.println("Akamai-X-WAF-Triggered-Rules=3912001:3912002:BOT-INTERSTITIAL-CHALLENGE   Delivery call No Cookie No JS");
                    }
                }


            }


            System.out.println("-------------------------------------------------");


        });



        System.out.println("Application url"+getApplicationUrl());
        s= getApplicationUrl();

        driver.get(s);
        Thread.sleep(5000);

        new WebDriverWait(driver,Duration.ofSeconds(30)).until(ExpectedConditions.titleIs("Access Denied"));

        // wsa();



    }




    @AfterMethod
    public void teardown()
    {
        devTool.close();
        driver.close();
    }

    @AfterSuite
    public void Tear() {

        driver.quit();

    }



}

