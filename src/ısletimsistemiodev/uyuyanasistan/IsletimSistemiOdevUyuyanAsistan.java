/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ısletimsistemiodev.uyuyanasistan;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jan
 */
public class IsletimSistemiOdevUyuyanAsistan {

    public static void main(String a[]) {
        System.out.println("Asistan uyuyor");
        
        Office office = new Office();

        Asistan asistan = new Asistan(office);
        OgrenciGenerator ogrenciGenerator = new OgrenciGenerator(office);

        Thread asistanThread = new Thread(asistan);
        Thread ogrenciGeneratorThread = new Thread(ogrenciGenerator);
        asistanThread.start();
        ogrenciGeneratorThread.start();
    }
    
}

class Asistan implements Runnable {

    Office office;

    public Asistan(Office office) {
        this.office = office;
    }

    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        while (true) {
            office.takeCareStudent();
        }
    }
}

class Ogrenci implements Runnable {

    String name;
    Date time;

    Office office;

    public Ogrenci(Office office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public Date getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void run() {
        asistanaGit();
    }

    private synchronized void asistanaGit() {
        office.add(this);
    }
}

class OgrenciGenerator implements Runnable {

    Office office;

    public OgrenciGenerator(Office office) {
        this.office = office;
    }

    public void run() {
        while (true) {
            Ogrenci ogrenci = new Ogrenci(office);
            ogrenci.setTime(new Date());
            Thread ogrenciThread = new Thread(ogrenci);
            ogrenci.setName(ogrenciThread.getId() + " numaralı öğrenci");
            ogrenciThread.start();

            try {
                TimeUnit.SECONDS.sleep((long)(Math.random()*10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class Office {

    int sandalyeSayisi;
    List<Ogrenci> listOgrenci;

    public Office() {
        sandalyeSayisi = 4;
        listOgrenci = new LinkedList<Ogrenci>();
    }

    boolean uyuyorMu = true;
    
    public void takeCareStudent() {
        Ogrenci ogrenci;
        
        synchronized (listOgrenci) {
            while (listOgrenci.size() == 0) {
                System.out.println("Asistan uyuyor");
                uyuyorMu = true;
                
                try {
                    listOgrenci.wait();
                } 
                catch (InterruptedException iex) {
                    iex.printStackTrace();
                }
            }
            
            if(uyuyorMu) {
                uyuyorMu = false;
            }
            else {
                System.out.println("Asistan bekleyen öğrenci olduğunu gördü");
            }
            
            ogrenci = (Ogrenci) ((LinkedList<?>) listOgrenci).getFirst();
        }
        
        long duration = 0;
        
        try {
            System.out.println("Asistan " + ogrenci.getName() + " ile ilgileniyor");
            duration = (long) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        } 
        catch (InterruptedException iex) {
            iex.printStackTrace();
        }
        
        System.out.println("Asistan " + ogrenci.getName() + "nin sorununu " + duration + " saniyede çözdü");
        ogrenci = (Ogrenci) ((LinkedList<?>) listOgrenci).poll();
    }

    public void add(Ogrenci ogrenci) {
        System.out.println(ogrenci.getName() + " " + ogrenci.getTime() + "'de ofise geldi.");

        synchronized (listOgrenci) {
            if (listOgrenci.size() == sandalyeSayisi) {
                System.out.println(ogrenci.getName() + " için boş sandalye yok");
                System.out.println(ogrenci.getName() + " geri dönüyor");
                return;
            }
            
            ((LinkedList<Ogrenci>) listOgrenci).offer(ogrenci);
            
            if (listOgrenci.size() == 1) {
                System.out.println(ogrenci.getName() + " asistanı uyandırıyor");
                listOgrenci.notify();
            }
            else{
                System.out.println(ogrenci.getName() + " ofis dışında sandalyelerden birine oturuyor ve beklemeye başlıyor");
            }
        }
    }
}
