
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author PabloJavier
 */
public class Compilador {
    
    public static ArrayList<String> instrucciones;
    public static char[] codigo;
    
    
    public static boolean analizar(String input, String nombre){
        
        codigo = obtenerCodigo(input); 
        
        if(!parentesisConcuerdan()){
            return false;
        }
        
        System.out.println(retornarCodigo());
        
        generarCodigo();
        
        generarArchivo(nombre);
        
        return true;
    }
    
    //R4 puntero de datos
    //PC puntero de instruccion
    
    public static void generarCodigo(){
        instrucciones = new ArrayList();
        Stack<Integer> stack = new Stack();
        
        //Codigo inicial
        instrucciones.add(".globl main");
        instrucciones.add("main:");
        instrucciones.add("LDR R4 ,= _array");
        instrucciones.add("push {lr}");
        
        
        //Instrucciones
        for(int i = 0; i < codigo.length; i++ ){
            char caracter = codigo[i];
            
            if(caracter == '>'){
                instrucciones.add("\tADD R4, R4, #1");
            }else if(caracter == '<'){
                instrucciones.add("\tSUB R4, R4, #1");
            }else if(caracter == '+'){
                instrucciones.add("\tLDRB R5, [R4]");
                instrucciones.add("\tADD R5, R5, #1");
                instrucciones.add("\tSTRB R5, [R4]");
            }else if(caracter == '-'){
                instrucciones.add("\tLDRB R5, [R4]");
                instrucciones.add("\tSUB R5, R5, #1");
                instrucciones.add("\tSTRB R5, [R4]");
            }else if(caracter == '.'){
                instrucciones.add("\tLDR R0 ,= _char ");
                instrucciones.add("\tLDRB R1, [R4]");
                instrucciones.add("\tBL printf");                
            }else if(caracter == ','){
                instrucciones.add("\tBL getchar");
                instrucciones.add("\tSTRB R0, [R4]");
            }else if(caracter == '['){
                stack.push(i);
                instrucciones.add("_in_" + i + ":");
                instrucciones.add("\tLDRB R5, [R4]");
                instrucciones.add("\tCMP R5, #0");
                instrucciones.add("\tBEQ _out_" + i);
            }else if(caracter == ']'){
                int n = stack.pop();
                instrucciones.add("_out_" + n + ":");
                instrucciones.add("\tLDRB R5, [R4]");
                instrucciones.add("\tCMP R5, #0");
                instrucciones.add("\tBNE _in_" + n);
            }else {
                throw new UnsupportedOperationException("Instruccion desconocida");
            }
        }
        
        //Codigo final
        instrucciones.add("pop {pc}");
        instrucciones.add("");
        instrucciones.add(".data");
        instrucciones.add(".align 4");
        instrucciones.add("");
        instrucciones.add("_char: .asciz \"%c\"");
        instrucciones.add("");
        instrucciones.add("_array: .space 30000");
    }
    
    public static boolean parentesisConcuerdan(){
        int contador = 0;
        for(char caracter : codigo){
            if(caracter == '['){
                contador++;
            }
            else if(caracter == ']'){
                contador--;
            }
            if(contador < 0){
                return false;
            }
        }
        return contador == 0;
    }
    
    public static boolean esInstruccion(char caracter){
        return caracter == '>'
            || caracter == '<'
            || caracter == '+'
            || caracter == '-'
            || caracter == '.'
            || caracter == ','
            || caracter == '['
            || caracter == ']';
    }
    
    public static char[] obtenerCodigo(String codigo){
        char[] original = codigo.toCharArray();
        
        ArrayList<Character> resultado = new ArrayList<>();
        
        for(char caracter : original){
            if(esInstruccion(caracter)){
                resultado.add(caracter);
            }
        }
        char[] retorno = new char[resultado.size()];
        
        for(int i = 0; i < retorno.length; i++){
            retorno[i] = resultado.get(i);
        }
        
        return retorno;
    }
    
    public static String retornarCodigo(){
        String resultado = "";
        for(char caracter : codigo){
            resultado += caracter;
        }
        
        return resultado;
    }
    
    public static void generarArchivo(String nombre){
        if(nombre.contains(".")){
            nombre = nombre.split(".")[0];
            if(nombre.trim().equals("")){
                nombre = "Brainfuck";
            }
        }
        
        nombre = nombre + ".s";
        
        String texto = "";
        for(String linea : instrucciones){
            texto += linea + "\n";
        }
        
        //Generacion del archivo de salida, con la descripcion del automata
        File file = new File(nombre);
        FileWriter fw;
        try {
            fw = new FileWriter(file, false);
            fw.write(texto);
            fw.close();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }
}
