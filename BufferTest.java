import static org.junit.Assert.*;

import org.junit.Test;

public class BufferTest {
	@Test
	// Testa o construtor buffer(), o getNthLine() e o getCursor()
	public void testEmptyBuffer() {
		//Cria o buffer vazio
		Buffer buffer = new Buffer();
		
		//Verifica se o buffer vazio tem uma unica string vazia
		assertEquals("", buffer.getNthLine(0));
		
		//Verifica se o cursor começa na Posição linha = 0 e coluna = 0
		assertEquals(0, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
	}

	@Test
	// Testa o costrutor buffer(String)
	public void teststringBuffer() {
		//Cria um buffer com uma unica string com a frase "ola mundinho"
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se o buffer contem a string "ola mundinho"
		assertEquals("ola mundinho", buffer.getNthLine(0));
		
		//Verifica se o cursor ficou na posição final da string escrita neste caso  linhas = 0 e colunas = 12
		assertEquals(0, buffer.getCursor().line);
		assertEquals(12, buffer.getCursor().column);
	}

	@Test
	// Testa a função getAllLines()
	public void testgetAllLines() {
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se a função retorna a lista strings com só uma string
		assertEquals(1, buffer.getAllLines().size());
		assertEquals("ola mundinho", buffer.getAllLines().get(0).toString());
		
		//Acrescenta 4 novas linhas para verificar se retorna a lista de strings  para n strings
		buffer.insertLn();
		buffer.insertStr("a");
		buffer.insertLn();
		buffer.insertStr("b");
		buffer.insertLn();
		buffer.insertStr("c");
		buffer.insertLn();
		buffer.insertStr("d");
		assertEquals("a", buffer.getAllLines().get(1).toString());
		assertEquals("b", buffer.getAllLines().get(2).toString());
		assertEquals("c", buffer.getAllLines().get(3).toString());
		assertEquals("d", buffer.getAllLines().get(4).toString());
		assertEquals(5, buffer.getAllLines().size());
	}
	
	@Test
	//Testa a função testgetNthLine()
	public void testgetNthLine(){
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se a função retorna a string requerida
		assertEquals("ola mundinho", buffer.getNthLine(0));
		
		//Acrescenta 4 novas linhas para verificar se retorna a strings  requerida para n strings
		buffer.insertLn();
		buffer.insertStr("a");
		buffer.insertLn();
		buffer.insertStr("b");
		buffer.insertLn();
		buffer.insertStr("c");
		buffer.insertLn();
		buffer.insertStr("d");
		assertEquals("a", buffer.getNthLine(1));
		assertEquals("b", buffer.getNthLine(2));
		assertEquals("c", buffer.getNthLine(3));
		assertEquals("d", buffer.getNthLine(4));
	}

	@Test
	// testa a função getLinesCount()
	public void testgetLinesCount() {
		Buffer buffer = new Buffer("ola mundinho");
		//Verifica se retorna  número correto de linhas para 1 linha
		assertEquals(1, buffer.getLinesCount());
		
		//Verifica se retorna o número correto de linhas para 5 linhas
		buffer.insertLn();
		buffer.insertLn();
		buffer.insertLn();
		buffer.insertLn();
		assertEquals(5, buffer.getAllLines().size());
	}

	@Test(expected = IllegalArgumentException.class)
	// testa a função insertStr() que se conter \n retorna exceção
	public void testinsertStr() {
		Buffer buffer = new Buffer("ola mundinho");
		
		//Insere a string "tudo bem" em frente a string inicial "ola mundinho" e verifica se esta correto
		buffer.insertStr(" tudo bem?");
		assertEquals("ola mundinho tudo bem?", buffer.getNthLine(0));
		
		//Verifica se insere corretamente a string a meio da string já existente
		buffer.setCursor( new Position(0, 6));
		buffer.insertStr("AQUIII");
		assertEquals("ola muAQUIIIndinho tudo bem?", buffer.getNthLine(0)
				);
		
		//Verifica se dá erro quando encontra-se \n a meio de uma string que se quer inserir
		buffer.insertStr("erro\nerro");
		buffer.getNthLine(0);
	}

	@Test
	// testa a função insertLine()
	public void testinsertLine() {
		Buffer buffer = new Buffer("ola mundinho");
		//Verifica se insere uma linha corretamente
		buffer.insertLn();
		assertEquals(2, buffer.getAllLines().size());
		assertEquals("ola mundinho", buffer.getNthLine(0));
		assertEquals("", buffer.getNthLine(1));
		
		//Verifica se o cursor moveu-se para a o sitio certo
		assertEquals(1, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
		
		//Verifica se for pedido para criar uma nova linha a meio de uma string o resto da string passa para a linha seguinte
		buffer.setCursor(new Position(0, 7));
		buffer.insertLn();
		assertEquals(3, buffer.getAllLines().size());
		assertEquals("ola mun", buffer.getNthLine(0));
		assertEquals("dinho", buffer.getNthLine(1));
		assertEquals(1, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
	}

	@Test
	// testa a função insertChar()
	public void testinsertChar() {
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se insere o caracter corretamente no fim da string
		buffer.insertChar('A');
		assertEquals("ola mundinhoA", buffer.getNthLine(0));
		buffer.setCursor(new Position(0, 6));
		
		//Verifica se insere o caracter corretamente no inicio da string
		buffer.insertChar('P');
		assertEquals("ola muPndinhoA", buffer.getNthLine(0));
	}

	@Test
	// Testa a função deleteChar()
	public void testdeleteChar() {
		Buffer buffer = new Buffer("ola mundinho");
		buffer.deleteChar();
		assertEquals("ola mundinh", buffer.getNthLine(0));
		assertEquals(0, buffer.getCursor().line);
		assertEquals(11, buffer.getCursor().column);
	}

	@Test
	// Testa a função deleteLn() e insertStr()
	public void testdeleteLn() {
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se elimina a linha vazia
		buffer.insertLn();
		assertEquals(2, buffer.getLinesCount());
		assertEquals("ola mundinho", buffer.getNthLine(0));
		assertEquals("", buffer.getNthLine(1));
		buffer.deleteLn();
		assertEquals(1, buffer.getLinesCount());
		assertEquals("ola mundinho", buffer.getNthLine(0));
		
		//Verifica se ao eliminar uma linha no inicio da string passa a string para o fim da linha anterior
		buffer.setCursor(new Position(0,0));
		buffer.insertLn();
		assertEquals("", buffer.getNthLine(0));
		assertEquals("ola mundinho", buffer.getNthLine(1));
		buffer.deleteLn();
		assertEquals(1, buffer.getLinesCount());
		assertEquals("ola mundinho", buffer.getNthLine(0));
	}

	@Test
	// Testa a função getCursor()
	public void testgetCursor() {
		Buffer buffer = new Buffer();
		//Verifica se retorna as posições corretas
		assertEquals(0, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
	}

	@Test
	// Testa a função setCursor()
	public void testsetCursor() {
		Buffer buffer = new Buffer("ola mundinho");
		buffer.setCursor(new Position(0, 7));
		//Verifica se o cursor foi colocado no sítio certo
		assertEquals(0, buffer.getCursor().line);
		assertEquals(7, buffer.getCursor().column);
	}

	@Test
	// Testa a função validPos()
	public void testvalidPos() {
		Buffer buffer = new Buffer("ola mundinho");
		//Verifica uma linha,coluna e linha e coluna que não existe se dá falso
		assertFalse(buffer.validPos(new Position(-1, 7)));
		assertFalse(buffer.validPos(new Position(0, 15)));
		assertFalse(buffer.validPos(new Position(1, 7)));
		
		//Verifica uma linha,coluna e linha e coluna que existe se dá verdade
		assertTrue(buffer.validPos(new Position(0, 12)));
		assertTrue(buffer.validPos(new Position(0, 0)));
		assertTrue(buffer.validPos(new Position(0, 7)));
	}

	 @Test
	// Testa a função movePrev()
	public void testmovePrev(){
		Buffer buffer = new Buffer("ola mundinho");
		
		//Verifica se anda um caracter atrás no fim da string
		assertEquals(0, buffer.getCursor().line);
		assertEquals(12, buffer.getCursor().column);
		buffer.movePrev();
		assertEquals(0, buffer.getCursor().line);
		assertEquals(11, buffer.getCursor().column);
		
		//Verifica se mantem na mesma posição no inicio do buffer
		buffer.setCursor(new Position(0, 0));
		buffer.movePrev();
		assertEquals(0, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
		
		//Verifica se volta para o fim da linha anterior
		buffer.setCursor(new Position(0, 12));
		buffer.insertLn();
		buffer.insertStr("tudo bem?");
		buffer.setCursor(new Position(1, 0));
		buffer.movePrev();
		assertEquals("ola mundinho", buffer.getNthLine(0));
		assertEquals("tudo bem?", buffer.getNthLine(1));
		assertEquals(0, buffer.getCursor().line);
		assertEquals(12, buffer.getCursor().column);
	}
	 
	 @Test
	 //Testa a função moveNext()
	 public void moveNext(){
		 Buffer buffer = new Buffer("ola mundinho");
		 
		//Verifica se anda um caracter a frenre no meio da string
		buffer.movePrev();
		assertEquals(0, buffer.getCursor().line);
		assertEquals(11, buffer.getCursor().column);
		buffer.moveNext();
		assertEquals(0, buffer.getCursor().line);
		assertEquals(12, buffer.getCursor().column);
		
		//Verifica se mantem na mesma posição no fim do buffer
		buffer.setCursor(new Position(0, 12));
		buffer.moveNext();
		assertEquals(0, buffer.getCursor().line);
		assertEquals(12, buffer.getCursor().column);
		
		//Verifica se volta para o inicio da linha seguinte
		buffer.setCursor(new Position(0, 12));
		buffer.insertLn();
		buffer.insertStr("tudo bem?");
		buffer.setCursor(new Position(0, 12));
		buffer.moveNext();
		assertEquals("ola mundinho", buffer.getNthLine(0));
		assertEquals("tudo bem?", buffer.getNthLine(1));
		assertEquals(1, buffer.getCursor().line);
		assertEquals(0, buffer.getCursor().column);
	 }
	 
	 @Test
	 //Testa a função movePrevLine()
	 public void testmovePrevLine(){
		 Buffer buffer = new Buffer("ola mundinho");
		 
			//Verifica se o cursor move-se para a linha anterior
			buffer.insertLn();
			buffer.insertStr("tudo bem?");
			buffer.movePrevLine();
			assertEquals("ola mundinho", buffer.getNthLine(0));
			assertEquals("tudo bem?", buffer.getNthLine(1));
			assertEquals(0, buffer.getCursor().line);
			assertEquals(9, buffer.getCursor().column);
			
			//Verifica se o cursor mantem-se no mesmo sitio se estiver na primeira linha
			buffer.movePrevLine();
			assertEquals(0, buffer.getCursor().line);
			assertEquals(9, buffer.getCursor().column);
			
			//Verifica se se move para o final da linha anterior caso seja mais pequena
			buffer.setCursor(new Position(1,0));
			buffer.insertStr("Esta ");
			buffer.setCursor(new Position(1,14));
			assertEquals("ola mundinho", buffer.getNthLine(0));
			assertEquals("Esta tudo bem?", buffer.getNthLine(1));
			assertEquals(1, buffer.getCursor().line);
			assertEquals(14, buffer.getCursor().column);
			buffer.movePrevLine();
			assertEquals(0, buffer.getCursor().line);
			assertEquals(12, buffer.getCursor().column);
	 }
	
	 @Test
	 //Testa a função moveNextLine()
	 public void testmoveNextLine(){
		 Buffer buffer = new Buffer("ola mundinho");
		 
			//Verifica se o cursor move-se para a linha seguinte
			buffer.insertLn();
			buffer.insertStr("tudo bem?");
			assertEquals("ola mundinho", buffer.getNthLine(0));
			assertEquals("tudo bem?", buffer.getNthLine(1));
			buffer.setCursor(new Position(0,8));
			buffer.moveNextLine();
			assertEquals(1, buffer.getCursor().line);
			assertEquals(8, buffer.getCursor().column);
			
			//Verifica se o cursor mantem-se no mesmo sitio se estiver na ultima linha
			buffer.moveNextLine();
			assertEquals(1, buffer.getCursor().line);
			assertEquals(8, buffer.getCursor().column);
			
			//Verifica se se move para o final da linha seguinte caso seja mais pequena
			buffer.setCursor(new Position(0,12));
			buffer.moveNextLine();
			assertEquals(1, buffer.getCursor().line);
			assertEquals(9, buffer.getCursor().column);
	 }	
}
