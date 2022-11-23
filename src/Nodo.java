public class Nodo{
    int distancia;
    int padre;
    int idNodo;
    boolean visitado;
    boolean visitadoF;
    public Nodo(int distancia, int padre, int idNodo){
        this.distancia = distancia;
        this.padre = padre;
        this.idNodo = idNodo;
        this.visitado = false;
        this.visitadoF = false;

    }

}