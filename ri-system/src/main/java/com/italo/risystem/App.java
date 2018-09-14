package com.italo.risystem;

import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;

/**
 * Hello world!
 *
 */
public class App {

    private static final int CREATE_BASE = 1;
    private static final int MAKE_QUERY = 2;
    private static final int QUIT = 3;
    private static Scanner input = new Scanner(System.in);
    private static int menu;

    private static void printMenu() {
        System.out.println("*********IR SYSTEM*********");
        System.out.println("1 - Create a new base");
        System.out.println("2 - Make a new query");
        System.out.println("3 - Quit");
        System.out.println("***************************");
        System.out.print("Choose an option: ");
    }

    private static void createBase() throws IOException {
        String option;
        String path;
        Indexer indexer;
        boolean stopword, stemming;

        System.out.print("Enter the path which the index will be saved: ");
        path = input.nextLine();
        indexer = new Indexer(path);

        System.out.print("Stopwords? (y / n): ");
        option = input.nextLine();
        stopword = option.equalsIgnoreCase("y");
        System.out.print("Stemming? (y / n): ");
        option = input.nextLine();
        stemming = option.equalsIgnoreCase("y");
        indexer.setAnalyzer(stopword, stemming);

        Analyzer analyzer = indexer.getAnalyzer();
        indexer.setWriter(analyzer);

        System.out.print("Enter the path which contains the files that will be indexed: ");
        path = input.nextLine();
        int numDocs = indexer.createIndex(path);
        System.out.println(numDocs + " indexed files!");
        
        indexer.closeWriter();
    }

    public static void main(String[] args) throws IOException {
        String option;

        System.out.println("Hello World!");
        while (menu != QUIT) {
            printMenu();
            option = input.nextLine();
            menu = Integer.parseInt(option);

            switch (menu) {
                case CREATE_BASE: {
                    createBase();
                    break;
                }
                case MAKE_QUERY: {
                    System.out.println("Making a query...");
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}