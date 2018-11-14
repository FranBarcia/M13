/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provawebservice;

/**
 *
 * @author montse.maqueda
 */
public class ProvaWebService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        WebServiceCalls calls = new WebServiceCalls();
        System.out.println(calls.comprovarLogin("fbarcia", "password"));
    }    
}
