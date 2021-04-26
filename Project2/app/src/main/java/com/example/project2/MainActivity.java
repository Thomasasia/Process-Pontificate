package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Debug;
import android.os.StrictMode;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends ListActivity {

    private int pid;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //obtain processes
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if(runningProcesses != null && runningProcesses.size() > 0){
            setListAdapter(new ListAdapter(this, runningProcesses));
        }
        else{
            Toast.makeText(getApplicationContext(), "No processes running!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        long send = 0;
        long recived = 0;

        int uid = ((RunningAppProcessInfo)getListAdapter().getItem(position)).uid;
        int pid = ((RunningAppProcessInfo)getListAdapter().getItem(position)).pid;

        recived = TrafficStats.getUidRxBytes(uid);
        send = TrafficStats.getUidTxBytes(uid);

        Toast.makeText(getApplicationContext(), "PID " + pid + " \n SLAIN", Toast.LENGTH_LONG).show();
        //android.os.Process.killProcess(pid); // gay and does not work
        kill(pid);
        updateProcesses();
    }

    public void genocide(View view){
        Toast.makeText(getApplicationContext(), "RIP AND TEAR!", Toast.LENGTH_SHORT).show();
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        int me=android.os.Process.myPid();
        for(RunningAppProcessInfo p : runningProcesses){
            if(p.pid != me)
                kill(p.pid);
        }
        // go through list, delete each process other than this one
        updateProcesses();
    }

    public void roulette(View view){

        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        Random r = new Random();

        int val = r.nextInt(runningProcesses.size());
        // delete a random process

        kill(runningProcesses.get(val).pid);
        updateProcesses();
        Toast.makeText(getApplicationContext(), "THE DICE DICED " + val, Toast.LENGTH_SHORT).show();

        // get info about the system
        String policy = StrictMode.getThreadPolicy().toString();
        Debug.MemoryInfo mem = new Debug.MemoryInfo();

        int dirty =	mem.getTotalPrivateDirty();

        int pss =	mem.getTotalPss();

        int sdirty =	mem.getTotalSharedDirty();


        Toast.makeText(getApplicationContext(), "Thread Policy : " + policy + "\nDirty Mem : " + dirty + "\nPSS : " + pss + "\n Shared Dirty Mem : " + sdirty, Toast.LENGTH_SHORT).show();


    }

    private void kill(int pid){
        long oldmem = Runtime.getRuntime().freeMemory();
        String cmd ="su -c kill -9 " + Integer.toString(pid);
        System.out.println(cmd);
        try {
            Runtime.getRuntime().exec (cmd); // hella sick and should fucking work
        } catch (IOException e) {
            e.printStackTrace();
        }
        long newmem = Runtime.getRuntime().freeMemory();
        Toast.makeText(getApplicationContext(), "Memory saved: " + (oldmem - newmem), Toast.LENGTH_SHORT).show();
    }

    private void updateProcesses(){
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if(runningProcesses != null && runningProcesses.size() > 0){
            setListAdapter(new ListAdapter(this, runningProcesses));
        }
        else{
            Toast.makeText(getApplicationContext(), "No apps running!", Toast.LENGTH_LONG).show();
        }
    }
}

