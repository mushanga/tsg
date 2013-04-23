package jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.CurrentTermin;
import models.Report;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.Mail;
import util.PropsConfigMgrImpl;

@Every("1mn")
public class GetFreeDay extends Job {
   
   private static HashMap<String,Integer> monthVals = new HashMap<String, Integer>();
   
   @Override
   public void doJob() throws Exception {

      super.doJob();
      
      if(CurrentTermin.findAll().size()<1){
         CurrentTermin ct = new CurrentTermin();
         ct.currentDate = 716;
         ct.save();
      }
      
      monthVals.put("April", 400);
      monthVals.put("Mai", 500);
      monthVals.put("Juni", 600);
      monthVals.put("Juli", 700);
      
      
      
      HtmlUnitDriver driver = new HtmlUnitDriver();
      
      driver.setJavascriptEnabled(true);
      
      // And now use this to visit Google
      driver.get("https://formular.berlin.de/jfs/findform?shortname=OTVBerlin&formtecid=4&areashortname=LABO");

      String res = driver.getPageSource();
      
      
      
      // Find the text input element by its name
      WebElement element = driver.findElement(By.id("btnTerminBuchen"));

      // Enter something to search for
//      element.sendKeys("Cheese!");

      // Now submit the form. WebDriver will find the form for us from the element
      element.click();
      
      
      Select select = new Select(driver.findElement(By.id("cobStaat")));
      select.selectByValue("163");
      
      select = new Select(driver.findElement(By.id("cobAnliegen")));
      select.selectByValue("324659");
      
      select = new Select(driver.findElement(By.id("cobSachgebiet")));
      select.selectByValue("1");

      element = driver.findElement(By.id("cbZurKenntnis"));
      element.click();
      
      element = driver.findElement(By.id("labNextpage"));
      element.click();

      element = driver.findElement(By.id("tfFirstName"));
      element.sendKeys("Ali Cagdas");

      element = driver.findElement(By.id("tfLastName"));
      element.sendKeys("Özek");

      select = new Select(driver.findElement(By.id("cobGebDatumTag")));
      select.selectByValue("14");
      
      select = new Select(driver.findElement(By.id("cobGebDatumMonat")));
      select.selectByValue("4");
      
      element = driver.findElement(By.id("tfGebDatumJahr"));
      element.sendKeys("1986");

      select = new Select(driver.findElement(By.id("cobVPers")));
      select.selectByValue("1");

      element = driver.findElement(By.id("tfMail"));
      element.sendKeys("cagdasozek@gmail.com");
      
      element = driver.findElement(By.id("tfEtNr"));
      element.sendKeys("D41064295");

      element = driver.findElement(By.id("txtNextpage"));
      element.click();
      

      String month = "";
      String report = "";
      int i = 0;
      do{
         month = driver.findElementByCssSelector("#month span").getText();
           
         report = report +"\n"+ month+"\n";
         List<String> freeDays = getAvailableDays(driver, true);
 
         
         report = report + StringUtils.join(freeDays, ", ");

         report = report +"\n";
         
         element = driver.findElement(By.id("labnextMonth"));
         element.click();
         i++;
      }while(!month.equalsIgnoreCase("Juli") && i<10);
      
      Report rep = new Report(report);
      rep.save();
      
      
   }
   public static void main(String[] args) throws InterruptedException {
      // Create a new instance of the html unit driver
      // Notice that the remainder of the code relies on the interface, 
      // not the implementation.
//      WebDriver driver = new HtmlUnitDriver();

     
 
  }

   @play.mvc.Util
   private static List<String> getAvailableDays(HtmlUnitDriver driver, boolean submit) throws EmailException{
      String month = driver.findElementByCssSelector("#month span").getText();
      
      ArrayList<String> days = new ArrayList<String>();
      List<WebElement> elements = driver.findElementsByCssSelector(".CELL a");
      for(WebElement element : elements){
         String style = element.getAttribute("style");
         if(style.contains("color: rgb(0,0,255)")){
            days.add(element.getText());  
            if(submit){
               CurrentTermin ct = (CurrentTermin) CurrentTermin.findAll().get(0);
               int day = Integer.valueOf(element.getText());
               int score = monthVals.get(month)+day;

               if (score < ct.currentDate) {
                  
                  
                  element.click();
                  
                  WebElement checkbx = driver.findElement(By.id("timeRow1Cb_0"));
                  
                  checkbx.click();
                  
                  WebElement next = driver.findElement(By.id("txtNextpage"));
                  next.click();
                  
                  ct.currentDate = score;
                  ct.save();
                  
                  SimpleEmail email = new SimpleEmail();
                  email.setFrom(Play.configuration.getProperty("mail.smtp.user"));
                  email.addTo(PropsConfigMgrImpl.getInstance().getMailRecipients()[0]);
                  email.setSubject("free day!");
                  email.setMsg(String.valueOf(score));
                  Mail.send(email); 
               }
               
            }
         }
      }
      
      return days;
      
   }
   
}