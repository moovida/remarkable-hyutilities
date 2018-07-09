package com.hydrologis.remarkable.progress;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class ModalProgressDemo {
    static JFrame frame;
    static Runnable heavyRunnable = new Runnable(){
        public void run() {
            ProgressMonitor monitor = ProgressUtil.createModalProgressMonitor(frame, 100, false, 10);
            monitor.start("Fetching 1 of 10 records from database...");
            try {
                for( int i = 0; i < 10; i += 1 ) {
                    fetchRecord(i);
                    monitor.setCurrent("Fetching " + (i + 1) + " of 10 records from database", (i + 1) * 10);
                }
            } finally {
                // to ensure that progress dlg is closed in case of any exception
                if (monitor.getCurrent() != monitor.getTotal())
                    monitor.setCurrent(null, monitor.getTotal());
            }
            heavyAction.setEnabled(true);
        }

        private void fetchRecord( int index ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    static Action heavyAction = new AbstractAction("Databse Query"){
        public void actionPerformed( ActionEvent e ) {
            setEnabled(false);
            new Thread(heavyRunnable).start();
        }
    };

    public static void main( String args[] ) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame("Modal Progress Dialog - santhosh@in.fiorano.com");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JButton(heavyAction));
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}