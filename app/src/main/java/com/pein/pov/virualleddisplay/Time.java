package com.pein.pov.virualleddisplay;

import java.util.Calendar;

/**
 * Created by Deshan on 6/30/2016.
 */
public class Time  extends Thread{
    String timeString;


    public void run(){
        while (true) {
            timeString = makeTimeString();
            timeString = "@t" + timeString + "#";
            System.out.print(timeString);
            synchronized (MainActivity.lock) {
                if (MainActivity.timeDisp) {
                    BluetoothHandle.write(timeString);
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String makeTimeString(){
        Calendar cal = Calendar.getInstance();
        int sec = cal.get(Calendar.SECOND);
        int hr = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int am = cal.get(Calendar.AM);
        String hour = "";
        String minute = "";
        String seconds = "";

        if(am!=1){
            if(hr!=12){
                hr=hr+12;
            }
        }

        if(hr<10)
            hour = "0"+hr;
        else
            hour = hr+"";

        if(min<10)
            minute = "0"+min;
        else
            minute = min+"";

        if(sec<10)
            seconds = "0"+sec;
        else
            seconds = sec+"";

        return hour+minute+seconds;
    }
}
