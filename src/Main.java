import Interfaz.InterfazTerminal;
import Interfaz.Ventana;

public class Main {

	public static void main(String[] args) {
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("gui")){
				new Ventana();
			}
		}

		new InterfazTerminal();
	}

}
