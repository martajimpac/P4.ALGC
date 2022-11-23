import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.abs;

/**
 * Implementar la búsqueda de manera bidireccional, es decir, empezando
 */
public class Main {
    static final int NUMEROFILAS = 100;//165 max
    static final int NUMEROCOLUMNAS = 100;
    static final int NUMERONODOS = NUMEROFILAS * NUMEROCOLUMNAS;
    static final int PARED = -10;
    static final int ESPACIO = 0;
    static final int INICIO = -4;
    static final int FINAL = -2;
    static final int CAMINO = -1;
    static final int ENCUENTRO = -5;
    static final int INFINITO = 999999999;

    //Calcular el id del nodo segun la fila y la columna en la que se encuentra
    public static int idNodo(int fila,int columna){
        int idNodo = fila * NUMEROCOLUMNAS + columna;
        return(idNodo);
    }
    public static void dibujarMapa(double[][] representar,String nombreFich) throws IOException {
        HeatChart map = new HeatChart(representar);
        Color lightYellow=new Color(255, 255, 70);
        Color darkPink=new Color(153, 0, 76);
        map.setLowValueColour(lightYellow);
        map.setHighValueColour(darkPink);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.7);

        map.saveToFile(new File(nombreFich));
    }

    /**
     * Generar laberintos utilizando listas de adyacencia
     * @param probabilidad
     * @param semilla1
     * @param semilla2
     * @return
     */
    public static Grafo generarLaberintosListas(double probabilidad, int semilla1, int semilla2){
        Nodo N[]= new Nodo[NUMERONODOS];
        int[][] E= new int[NUMERONODOS][NUMERONODOS]; //matriz de adyacencias

        Random rnd1 = new Random(semilla1); //inicializar rand-float con semilla
        Random rnd2 = new Random(semilla2); //rand float para inicializar pesos

        int peso;
        for(int i=0; i<NUMEROFILAS;i++){
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {
                N[idNodo(i,j)] = new Nodo(INFINITO,-1,idNodo(i,j));//inicilizar  nodo
                double random1= rnd1.nextDouble(); //genera un entero entre 0 y 1
                if(i>0 && random1 < probabilidad){
                    peso = abs(rnd2.nextInt()) % 12 + 1;
                    E[idNodo(i,j)][idNodo(i-1,j)] = peso;
                    E[idNodo(i-1,j)][idNodo(i,j)] = peso;
                }
                if(j>0 && random1 < probabilidad){
                    peso = abs(rnd2.nextInt()) % 12 + 1;
                    E[idNodo(i,j)][idNodo(i,j-1)] = peso;
                    E[idNodo(i,j-1)][idNodo(i,j)] = peso;
                }
            }
        }
        Grafo grafo = new Grafo(N,E);
        return(grafo);
    }

    /**
     * Dibujar los laberintos en una matriz para luego poder representarlos
     * @param grafo
     * @param salida
     * @param destino
     * @param idNodoEncuentro
     * @return
     */
    public static double[][] dibujarLaberintosListas(Grafo grafo,int salida, int destino,int idNodoEncuentro) {
        Nodo nodoMenor;
        //Crear e Inicializar matriz de mapa de calor con ceros (paredes)
        double[][] representar = new double[NUMEROFILAS * 2 + 1][NUMEROCOLUMNAS * 2 + 1];
        for (int i = 0; i < NUMEROFILAS * 2 + 1; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS * 2 + 1; j++) {
                representar[i][j] = PARED;
            }
        }

        //Recorrer los índices originales de las habitaciones
        for (int i = 0; i < NUMEROFILAS; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {
                if(grafo.N[idNodo(i,j)].distancia==INFINITO){
                    grafo.N[idNodo(i,j)].distancia=0;
                }

                //PONER HABITACIONES
                if(grafo.N[idNodo(i,j)].distancia != 0){ //si ha sido visitado pintar con la distancia
                    representar[i * 2 + 1][j * 2 + 1] = grafo.N[idNodo(i,j)].distancia;
                }else{
                    representar[i * 2 + 1][j * 2 + 1] = ESPACIO;
                }
                if(grafo.N[idNodo(i,j)].idNodo == destino){ //si ha sido visitado pintar con la distancia
                    representar[i * 2 + 1][j * 2 + 1] = FINAL;
                }
                if(grafo.N[idNodo(i,j)].idNodo == salida){ //si ha sido visitado pintar con la distancia
                    representar[i * 2 + 1][j * 2 + 1] = INICIO;
                }
                if(idNodoEncuentro!=-100){
                    if(grafo.N[idNodo(i,j)].idNodo == idNodoEncuentro){ //si ha sido visitado pintar con la distancia
                        representar[i * 2 + 1][j * 2 + 1] = ENCUENTRO;
                    }
                }
                //PONER PASILLOS Y PINTAR RECORRIDOS
                //Hacia la derecha
                if (j < NUMEROCOLUMNAS-1) { //saltamos la ultima columna de habitaciones
                    if(grafo.E[idNodo(i, j)][idNodo(i, j + 1)] !=0) { //Pintar pasillos con sus pesos
                        nodoMenor = grafo.N[idNodo(i,j)];
                        if(grafo.N[idNodo(i,j)].distancia>grafo.N[idNodo(i,j+1)].distancia) {
                            //nos quedamos con el que tenga menor distancia
                            nodoMenor = grafo.N[idNodo(i,j+1)];
                        }
                        representar[i * 2 + 1][j * 2 + 2] = nodoMenor.distancia;
                    }
                }

                //Hacia la abajo
                if (i < NUMEROFILAS-1) { //saltamos la ultima fila de habitaciones
                    if(grafo.E[idNodo(i, j)][idNodo(i+1, j)] !=0) { //Pintar pasillos con su s pesos
                        nodoMenor = grafo.N[idNodo(i,j)];
                        if(grafo.N[idNodo(i,j)].distancia>grafo.N[idNodo(i+1,j)].distancia) {
                            //nos quedamos con el que tenga menor distancia
                            nodoMenor = grafo.N[idNodo(i+1,j)];
                        }
                        representar[i * 2 + 2][j * 2 + 1] = nodoMenor.distancia;
                    }
                }
            }
        }
        return(representar);
    }

    public static int manhattan(int a, int b){
        int aX = a % NUMEROFILAS;
        int bX = b % NUMEROFILAS;
        int aY = a / NUMEROFILAS;
        int bY = b / NUMEROFILAS;
        return abs(aX - bX) + abs(aY - bY);
    }

    /**
     * Algoritmo para buscar el camino más corto entre dos nodos
     * @param grafo
     * @param nodoInicial
     * @param nodoFinal
     */
    public static void dijkstraFrontera(Grafo grafo, int nodoInicial, int nodoFinal) {
        ArrayList<Nodo> cola = new ArrayList<>();
        grafo.N[nodoInicial].distancia = 0;
        cola.add(grafo.N[nodoInicial]);
        int indice;
        Nodo nodo;
        int nuevaDistancia;

        while(!cola.isEmpty()) {
            nodo = new Nodo(INFINITO,0,0);
            for(Nodo n: cola){
                if(n.distancia<nodo.distancia){
                    nodo= n;
                }
            }
            indice = cola.lastIndexOf(nodo); //guardamos y eliminamos de cola el nodo más cercano
            nodo = cola.get(indice);
            cola.remove(indice);
            if(nodo.idNodo==nodoFinal){
                break;
            }
            nodo.visitado=true;
            //ver los hijos del nodo actual
            for(Nodo hijo:grafo.N) {
                if(grafo.E[nodo.idNodo][hijo.idNodo] != 0 && !hijo.visitado){ //hijos no visitados aun
                    nuevaDistancia = grafo.E[nodo.idNodo][hijo.idNodo] + grafo.N[nodo.idNodo].distancia;
                    if(nuevaDistancia<grafo.N[hijo.idNodo].distancia){
                        hijo.distancia= nuevaDistancia;
                        hijo.padre= nodo.idNodo;
                        cola.add(hijo);
                    }
                }
            }
        }
    }

    /**
     * Es igual que el algoritmo de Dijkstra pero utiliza una heuristica para guiar la búsqueda
     * @param grafo
     * @param nodoInicial
     * @param nodoFinal
     */
    public static void Aestrella (Grafo grafo, int nodoInicial, int nodoFinal) {

        //CREAR LAS COLAS Y AÑADIR EL NODO INICIAL DEL CAMINO
        ArrayList<Nodo> cola = new ArrayList<>();
        grafo.N[nodoInicial].distancia = 0;
        cola.add(grafo.N[nodoInicial]);
        int indiceCola;
        Nodo nodo;
        int nuevaDistancia;


        while(!cola.isEmpty()) {
            //BUSCAR NODO MÁS CERCANO AL NODO ACTUAL Y QUITARLO DE LA COLA: CAMINO DESDE EL NODO INICIAL
            nodo = new Nodo(INFINITO,0,0);//da igual lo que pongas en padre y idnodo
            for(Nodo n: cola){
                if(n.distancia<nodo.distancia){
                    nodo= n;
                }
            }
            indiceCola = cola.lastIndexOf(nodo); //guardamos y eliminamos de la cola el nodo más cercano
            cola.remove(indiceCola);

            if(nodo.idNodo==nodoFinal){
                break;
            }
            grafo.N[nodo.idNodo].visitado=true;

            //ver los hijos del nodo actual
            for(Nodo hijo:grafo.N) {
                if(grafo.E[nodo.idNodo][hijo.idNodo] != 0 && !hijo.visitado){ //hijos no visitados aun
                    nuevaDistancia = grafo.E[nodo.idNodo][hijo.idNodo] + grafo.N[nodo.idNodo].distancia + 3 * manhattan(nodo.idNodo,nodoFinal);
                    if(nuevaDistancia<grafo.N[hijo.idNodo].distancia){
                        hijo.distancia= nuevaDistancia;
                        hijo.padre= nodo.idNodo;
                        cola.add(hijo);
                    }
                }
            }
        }
    }

    //Método recursivo para buscar los caminos
    public static Grafo buscarCamino(Grafo grafo, int origen, int destino){
        int idPadre = grafo.N[destino].padre;
        if(origen==idPadre) return grafo;
        Grafo.N[idPadre].distancia= CAMINO;
        buscarCamino(grafo,origen,idPadre);
        return grafo;
    }

    public static void main(String[] args) throws IOException {
        //Parametros de entrada:
        boolean matrizEjes = false; //true matriz adyacencia, false listas adyacencia
        double probabilidad = 0.7; //numero entre 0 y 1
        int semilla1 = 3;
        int semilla2 = 4;
        int semilla3 = 0;

        System.out.println("Que algortimo quieres usar?");
        System.out.println("1: Dijkstra, 2: Aestrella");

        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            opcion = scanner.nextInt();
        }while (opcion <1 && opcion>2);

        Grafo grafo;
        double[][] representar = new double[NUMEROFILAS * 2 + 1][NUMEROCOLUMNAS * 2 + 1];

        Random rnd3 = new Random(semilla3);

        int salida = rnd3.nextInt(NUMERONODOS-1);
        int destino = rnd3.nextInt(NUMERONODOS-1);

        grafo = generarLaberintosListas(probabilidad,semilla1,semilla2);
        System.out.println("salida: "+ salida);
        System.out.println("final: " + destino);

        int idNodoEncuentro = -100;
        String algoritmo = "";
        switch(opcion){
            case 1:
                algoritmo = "Dijkstra";
                dijkstraFrontera(grafo,salida,destino);
                buscarCamino(grafo,salida,destino);
                representar = dibujarLaberintosListas(grafo,salida,destino,idNodoEncuentro);
            break;
            case 2:
                algoritmo = "AEstrella";
                Aestrella(grafo,salida,destino);
                buscarCamino(grafo,salida,destino);
                representar = dibujarLaberintosListas(grafo,salida,destino,idNodoEncuentro);
                break;
        }

        String nombreFich = algoritmo + " " + probabilidad + "-S1:" + semilla1 + "-S2:" + semilla2 +"-S3:" + semilla3 +"-F:" + NUMEROFILAS + "-C:" + NUMEROCOLUMNAS+ ".png";
        dibujarMapa(representar,nombreFich);
    }
}
