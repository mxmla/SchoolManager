package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;


public class HomeworkActivity extends ActionBarActivity implements DialogEditHomework.Communicator, DialogViewHomework.Communicator, HomeworkAdapter.Communicator{

    private Toolbar toolbar;
    private NavigationDrawerFragment1 drawerFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private CoordinatorLayout fabCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*Fragment mHomeworkFragment = getSupportFragmentManager().findFragmentByTag
                (HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);


        if (mHomeworkFragment == null){
            mHomeworkFragment = new HomeworkFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mHomeworkFragment,
                    HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }*/

        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new HomeworkPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_fragment_homework_pending).toString());
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.title_fragment_homework_done).toString());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.color_homework)}));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditHwDialog();
            }
        });

        fabCoordinator = (CoordinatorLayout) findViewById(R.id.fabCoordinator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homework, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent i2 = new Intent(this,
                        SettingsActivity.class);
                startActivityForResult(i2, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateViews(){
        int currentPage = viewPager.getCurrentItem();
        pagerAdapter = new HomeworkPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_fragment_homework_pending).toString());
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.title_fragment_homework_done).toString());
        viewPager.setCurrentItem(currentPage);
    }

    public void recyclerViewListClicked(String ID){
        showViewHwDialog(ID);
    }
    public void recyclerViewDoneClicked(String ID) {
        updateViews();
    }

    public void onDialogMessageViewHomework(boolean edit, String ID){
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/
        updateViews();

        if (edit){
            showEditHwDialogForExistingHw(ID);
        }
    }

    @Override
    public void onDialogMessageDeleteHomework(String ID) {
        deleteHomework(this, ID);
        updateViews();
    }

    @Override
    public void onDialogMessageDoneClicked() {
        updateViews();
    }

    public void showEditHwDialogForExistingHw(String ID){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putBoolean("existing", true);
        args.putString("subject", "-");
        args.putString("ID", ID);
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showViewHwDialog(String ID){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putString("ID", ID);
        FragmentManager manager = getSupportFragmentManager();
        DialogViewHomework myDialog = new DialogViewHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showEditHwDialog(){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putBoolean("existing", false);
        args.putString("subject", "-");
        args.putString("ID", "-");
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }


    @Override
    public void onDialogMessageHomework(String title, String content, String subject, String date) {
        createHomework(this, subject, date, title, content);

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/

        updateViews();
    }

    @Override
    public void onDialogMessageUpdateHomework(String ID, String title, String content, String subject, String date, String done) {
        updateHomework(this, ID, subject, date, title, content, done);

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/

        updateViews();
    }

    public void createHomework(Context context, String subject, String date, String hTitle, String hContent){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");

        // get the new ID
        int maxID = -1;
        for (int i1 = 0; i1 < Rows; i1++){
            int thisID = Integer.valueOf(myArray[i1][0]);
            if (thisID > maxID) maxID = thisID;
        }
        int ID = maxID + 1;

        // Save data to myArray
        String[][] myArray2 = new String[Rows + 1][Cols];
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < Cols; c++) {
                myArray2[r][c] = myArray[r][c];
            }
        }
        myArray2[Rows][0] = String.valueOf(ID);
        myArray2[Rows][1] = subject;
        myArray2[Rows][2] = date;
        myArray2[Rows][3] = hTitle;
        myArray2[Rows][4] = hContent;
        myArray2[Rows][5] = "no";
        myArray = myArray2;
        Rows++;

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHomework(Context context, String ID, String subject, String date, String hTitle, String hContent, String done){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");


        // Save data to myArray
        for (int r = 0; r < Rows; r++) {
            if (myArray[r][0].equals(ID)){
                myArray[r][1] = subject;
                myArray[r][2] = date;
                myArray[r][3] = hTitle;
                myArray[r][4] = hContent;
                myArray[r][5] = done;
            }
        }

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHomework(Context context, String ID){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");

        //Generate temporary backup array
        String[] backupArray = new String[6];

        //Remove data from myArray
        int removableRow = -1;
        for (int r = 0; r < Rows; r++) {
            if (myArray[r][0].equals(ID)){
                removableRow = r;
                backupArray[0] = myArray[r][0];
                backupArray[1] = myArray[r][1];
                backupArray[2] = myArray[r][2];
                backupArray[3] = myArray[r][3];
                backupArray[4] = myArray[r][4];
                backupArray[5] = myArray[r][5];
            }
        }
        if (removableRow >= 0) {
            Rows = Rows - 1;
            String[][] newArray = new String[myArray.length - 1][6];
            for (int i1 = 0; i1 < removableRow; i1++){
                newArray[i1][0] = myArray[i1][0];
                newArray[i1][1] = myArray[i1][1];
                newArray[i1][2] = myArray[i1][2];
                newArray[i1][3] = myArray[i1][3];
                newArray[i1][4] = myArray[i1][4];
                newArray[i1][5] = myArray[i1][5];
            }
            for (int i2 = removableRow + 1; i2 < myArray.length; i2++){
                newArray[i2-1][0] = myArray[i2][0];
                newArray[i2-1][1] = myArray[i2][1];
                newArray[i2-1][2] = myArray[i2][2];
                newArray[i2-1][3] = myArray[i2][3];
                newArray[i2-1][4] = myArray[i2][4];
                newArray[i2-1][5] = myArray[i2][5];
            }
            myArray = newArray;
        }

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Context fContext = context;
        final String fID = backupArray[0];
        final String fSubject = backupArray[1];
        final String fDate = backupArray[2];
        final String fTitle = backupArray[3];
        final String fContent = backupArray[4];
        final String fDone = backupArray[5];

        Snackbar
                .make(fabCoordinator, R.string.snackbar_homework_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_homework_deleted_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteUndo(fContext, fID, fSubject, fDate, fTitle, fContent, fDone);
                    }
                })
                .show();
    }

    public void deleteUndo(Context context, String ID, String subject, String date, String hTitle, String hContent, String done){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");


        // Save data to myArray
        String[][] newArray = new String[Rows+1][Cols];
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < Cols; c++){
                newArray[r][c] = myArray[r][c];
            }
        }
        newArray[Rows][0] = ID;
        newArray[Rows][1] = subject;
        newArray[Rows][2] = date;
        newArray[Rows][3] = hTitle;
        newArray[Rows][4] = hContent;
        newArray[Rows][5] = done;

        Rows = Rows + 1;

        myArray = newArray;

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        updateViews();
        //Snackbar.make(fabCoordinator, R.string.snackbar_homework_deleted_restored, Snackbar.LENGTH_SHORT).show();
    }

    public String[][] toArray(Context context, String filename){

        int Rows = nmbrRowsCols(context, filename)[0];
        int Cols = nmbrRowsCols(context, filename)[1];
        String InputLine = "";
        Scanner scanIn = null;

        // Read existing file
        String[][] myArray = new String[Rows][Cols];
        int Rowc = 0;
        scanIn = null;
        InputLine = "";


        File file = new File(getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));

            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                for (int x = 0; x < InArray.length; x++){
                    myArray[Rowc][x] = String.valueOf(InArray[x]);
                }
                Rowc++;
            }
            scanIn.close();
        }catch (Exception e){
            System.out.println(e);
        }

        return myArray;
    }

    public int[] nmbrRowsCols(Context context, String filename){
        String InputLine = "";
        Scanner scanIn = null;

        // Get number of rows and columns
        int Rows = 0;
        int Cols = 0;

        File file = new File(getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));
            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                Rows++;
                Cols = InArray.length;
            }
            scanIn.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        int[] RowsCols = new int[2];
        RowsCols[0] = Rows;
        RowsCols[1] = Cols;
        return RowsCols;
    }
}
