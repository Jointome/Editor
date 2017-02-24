//import static org.junit.Assert*;
import java.util.LinkedList;

public class Buffer {
	private LinkedList<StringBuilder> lineList = new LinkedList<StringBuilder>();
	private Position cursor;

	// obter todas as linhas do texto
	public LinkedList<StringBuilder> getAllLines() {
		return lineList;
	}

	// obter uma linha especifica
	public String getNthLine(int index) {
		return lineList.get(index).toString();
	}

	// obter o número de linhas
	public int getLinesCount() {
		return lineList.size();
	}

	// construtor Buffer vazio
	Buffer() {
		StringBuilder emptyString = new StringBuilder();
		lineList.addLast(emptyString);
		cursor = new Position(0, 0);
	}

	// construtor buffer com uma string
	Buffer(String s) {
		cursor = new Position(0, s.length());
		lineList.addLast(new StringBuilder(s));
	}

	// inserir uma cadeia de texto na posiçao do cursor
	public void insertStr(String s) {
		if (!s.contains("\n")) {
			StringBuilder strin = lineList.get(cursor.line);
			strin.insert(cursor.column, s);
			cursor = new Position(cursor.line, cursor.column + s.length());
		} else
			throw new IllegalArgumentException("Buffer.insert: newline in text");
	}

	// quebrar a linha na posiçao do cursor
	public void insertLn() {
		StringBuilder strin = lineList.get(cursor.line);
		StringBuilder emptystrin = new StringBuilder(strin.substring(cursor.column));
		strin.delete(cursor.column,strin.length());
		strin = new StringBuilder(strin.substring(cursor.column));
		lineList.add(cursor.line + 1, emptystrin);
		cursor = new Position(cursor.line + 1, 0);
	}

	// inserir um caracter
	public void insertChar(char read) {
		if (read == '\n')
			insertLn();
		else {
			StringBuilder strin = lineList.get(cursor.line);
			strin.insert(cursor.column,read);
			cursor = new Position(cursor.line, cursor.column + 1);
		}
	}

	// apagar um caracter imediatamente anterior à posição do cursor
	public void deleteChar() {
		if (cursor.column > 0) {
			StringBuilder strin = lineList.get(cursor.line);
			strin.deleteCharAt(cursor.column - 1);
			cursor = new Position(cursor.line, cursor.column - 1);
		} else if (cursor.line > 0)
			deleteLn();

	}

	// juntar a linha do cursor com a anterior
	public void deleteLn() {
		StringBuilder strin = lineList.remove(cursor.line);
		StringBuilder prevstrin = lineList.get(cursor.line - 1);
		cursor = new Position(cursor.line - 1, prevstrin.length());
		prevstrin.insert(prevstrin.length(), strin);
	}

	// ----------------------------------------------------------
	// funções para obter e mover o cursor
	// ----------------------------------------------------------

	// obter a posição do cursor (getcursor)
	public Position getCursor() {
		return cursor;
	}

	// mudar a posição lógica do cursor
	public void setCursor(Position move) {
		if (validPos(move))
			cursor = new Position(move.line, move.column);
		else
			throw new IllegalArgumentException(
					"Buffer.setCursor: invalid position");
	}

	// verificar se uma posição é valida
	public boolean validPos(Position move) {
		if (0 <= move.line && move.line < lineList.size() && 0 <= move.column
				&& move.column <= lineList.get(move.line).length())
			return true;
		return false;
	}

	// mover o cursor uma posição atrás
	public void movePrev() {
		if (cursor.column > 0)
			cursor = new Position(cursor.line, cursor.column - 1);
		else if (cursor.line > 0)
			cursor = new Position(cursor.line - 1, lineList
					.get(cursor.line - 1).length());
	}

	// mover o cursor uma posição à frente
	public void moveNext() {
		if (cursor.column < lineList.get(cursor.line).length())
			cursor = new Position(cursor.line, cursor.column + 1);

		else if (cursor.line + 1 < lineList.size())
			cursor = new Position(cursor.line + 1, 0);
	}

	// mover o cursor para a linha lógica anterior
	public void movePrevLine() {
		if (cursor.line > 0)
			if (cursor.column < lineList.get(cursor.line-1).length())
				cursor = new Position(cursor.line - 1, cursor.column);
			else
				cursor = new Position(cursor.line - 1, lineList.get(
						cursor.line - 1).length());
	}

	// mover o cursor para a linha lógica seguinte
	public void moveNextLine() {
		if (cursor.line + 1 < lineList.size())
			if (cursor.column < lineList.get(cursor.line+1).length())
				cursor = new Position(cursor.line + 1, cursor.column);
			else
				cursor = new Position(cursor.line + 1, lineList.get(
						cursor.line + 1).length());
	}
}
