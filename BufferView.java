import com.googlecode.lanterna.*;
import java.nio.file.Path;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
import java.util.LinkedList;

public class BufferView {
	int pos;
	private FileBuffer buffer;
	private Terminal term;
	private int startRow;
	private int height;
	public LinkedList<Integer> modifiedLines;
	private int line_height;
	private int total_line_hight;

	// Função que verifica e retorna uma posição visual a partir de uma lógica
	public int[] viewPos(int row, int col) {
		int coord[] = { -startRow - total_line_hight, 0 };
		for (int i = 0; i < row; i++) {
			coord[0] += ((buffer.getNthLine(i).length() / term
					.getTerminalSize().getColumns()) + 1);
		}
		int getit = ((col / term.getTerminalSize().getColumns()));
		coord[0] += getit;
		coord[1] = (col - (getit * term.getTerminalSize().getColumns()));
		return coord;
	}

	// Envia para a função show as linhas modificadas
	public void redraw() {
		int coord[] = new int[2];
		for (Integer line : modifiedLines) {
			coord = viewPos(line, 0);
			if (coord != null) {
				height = coord[0];
				show(buffer.getNthLine(line));
			}
		}
		modifiedLines.clear();
	}

	// Desenha as linhas modificadas
	private void show(String str) {
		int len = str.length();
		int j = 0;
		while (len > term.getTerminalSize().getColumns()) {
			clearLine(height);
			for (int i = 0; i < term.getTerminalSize().getColumns(); i++) {
				len--;
				term.putCharacter(str.charAt(j));
				j++;
			}
			height++;
			term.moveCursor(0, height);
		}
		clearLine(height);
		for (int i = 0; i < len; i++) {
			term.putCharacter(str.charAt(j));
			j++;
		}
	}

	// Função para limpar uma linha
	private void clearLine(int pos) {
		term.moveCursor(0, pos);
		for (int i = 0; i < term.getTerminalSize().getColumns(); i++)
			term.putCharacter(' ');
		term.moveCursor(0, pos);
	}

	// Função de visualização através do lanterna
	public BufferView(Path path) throws IOException {
		term = TerminalFacade.createTerminal();
		term.enterPrivateMode();
		modifiedLines = new LinkedList<Integer>();
		line_height = 0;
		startRow = 0;
		height = 0;
		total_line_hight = 0;
		buffer = new FileBuffer();
		buffer.open(path);
		for (int i = 0; i < buffer.getLinesCount(); i++)
			modifiedLines.addLast(new Integer(i));
		int coord[] = new int[2];
		redraw();

		while (true) {
			Key k = term.readInput();
			if (k != null) {
				switch (k.getKind()) {
				case Escape:
					buffer.save();
					term.exitPrivateMode();
					return;
				case ArrowLeft:
					// Move o cursor para a Esquerda
					buffer.movePrev();
					break;
				case ArrowRight:
					// Move o cursor para a direita
					buffer.moveNext();
					break;
				case ArrowDown:
					// Move o cursor para baixo
					Position al;
					// Verificar se a linha logica no momento tem mais linhas
					// virtuais para passar para a proxima linha visual e nao
					// logica
					if ((buffer.getNthLine(buffer.getCursor().line).length() - buffer
							.getCursor().column) > (term.getTerminalSize()
							.getColumns()) - 1) {
						al = new Position(buffer.getCursor().line,
								buffer.getCursor().column
										+ term.getTerminalSize().getColumns());
						buffer.setCursor(al);
					} else {
						coord = viewPos(buffer.getCursor().line,
								buffer.getCursor().column);
						// Verifica se a proxima linha visual que é a mesma
						// logica é menor que a atual
						if (term.getTerminalSize().getColumns() < (coord[1] + (buffer
								.getNthLine(buffer.getCursor().line).length() - buffer
								.getCursor().column))) {
							al = new Position(buffer.getCursor().line, buffer
									.getNthLine(buffer.getCursor().line)
									.length());
							buffer.setCursor(al);
						} else {
							coord = viewPos(buffer.getCursor().line,
									buffer.getCursor().column);
							// Verifica se a linha visual no momento é menor que
							// a linha lógica seguinte
							if ((buffer.getLinesCount() > buffer.getCursor().line + 1)
									&& (coord[1] < buffer.getNthLine(
											buffer.getCursor().line + 1)
											.length())) {
								al = new Position(buffer.getCursor().line + 1,
										coord[1]);
								buffer.setCursor(al);
							} else
								buffer.moveNextLine();
						}
					}
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					// Verifica se a posição está no fim do terminal
					if (coord[0] == (term.getTerminalSize().getRows())) {
						// Aumenta a linha visual para saber onde começa a
						// janela e termina
						if (buffer.getNthLine(startRow).length() < ((line_height + 1) * term
								.getTerminalSize().getColumns())) {
							startRow++;
							height = 0;
							// Faz a atualização da zona visual inteira
							for (int i = startRow - 1; i < ((term
									.getTerminalSize().getRows()) + startRow); i++) {
								if (i < buffer.getLinesCount()) {
									coord = viewPos(i, 0);
									if (coord[0] < term.getTerminalSize()
											.getRows())
										modifiedLines.addLast(i);
									else
										break;
								} else
									break;
							}
							line_height = 0;
						} else {
							total_line_hight++;
							line_height++;
							String toShow = buffer.getNthLine(startRow)
									.substring(
											line_height
													* term.getTerminalSize()
															.getColumns());
							height = 0;
							show(toShow);
							for (int i = startRow; i < buffer.getLinesCount(); i++)
								modifiedLines.addLast(i);
						}

					}
					break;
				case ArrowUp:
					// Mover cursor para cima
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					// Verifica se o cursor não está no inicio das linhas
					// lógicas e se está no inicio das linhas visuais
					if (startRow > 0 && coord[0] == 0) {
						int getCoord = buffer.getNthLine(startRow - 1).length()
								/ term.getTerminalSize().getColumns();
						getCoord = getCoord - line_height;
						if (getCoord == 0) {
							// diminui a posição das linhas visuais nas linhas
							// lógicas
							startRow--;
							line_height = 0;
							height = 0;
							// Verifica se o terminal é maior que a quantidade
							// de
							// linhas lógicas. Atualiza do inicio das linhas
							// visuais
							// até ao fim das lógicas
							if ((term.getTerminalSize().getRows()) > buffer
									.getLinesCount()) {
								for (int i = startRow; i < buffer
										.getLinesCount(); i++)
									modifiedLines.addLast(i);
								// Limpa o resto das linhas que falta
								for (int i = buffer.getLinesCount(); i < term
										.getTerminalSize().getRows(); i++)
									clearLine(i);
							} else {
								// Verifica se o cursor não está no inicio das
								// linhas lógicas e atualiza até ao fim do
								// terminal
								// desde linha anteri
								if (startRow != 0) {
									for (int i = startRow - 1; i < ((term
											.getTerminalSize().getRows()) + startRow); i++) {
										coord = viewPos(i, 0);
										if (coord[0] < term.getTerminalSize()
												.getRows())
											modifiedLines.addLast(i);
									}
								} else {
									// Atualiza a partir da mesma linha lógica
									// no
									// caso da linha lógica ser ter 2 ou mais
									// linhas
									// vizuais
									for (int i = startRow; i < ((term
											.getTerminalSize().getRows()) + startRow); i++) {
										coord = viewPos(i, 0);
										if (coord[0] < term.getTerminalSize()
												.getRows())
											modifiedLines.addLast(i);
									}
								}
							}
						} else {
							total_line_hight--;
							line_height++;
							String toShow = buffer.getNthLine(startRow - 1)
									.substring(
											getCoord
													* term.getTerminalSize()
															.getColumns());
							height = 0;
							show(toShow);
							for (int i = startRow; i < buffer.getLinesCount(); i++)
								modifiedLines.addLast(i);
						}
					}
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					int j = coord[0];
					coord = viewPos(buffer.getCursor().line, 0);
					// Verifica se vai mudar de linha lógica ou manteve
					if (coord[0] != j) {
						al = new Position(buffer.getCursor().line,
								buffer.getCursor().column
										- term.getTerminalSize().getColumns());
						buffer.setCursor(al);
					} else {
						// Verifica se não é a linha lógica 0 e se a linha
						// anterior tem mais do que uma linha visual
						if ((0 < buffer.getCursor().line)
								&& buffer.getNthLine(
										buffer.getCursor().line - 1).length() > (term
										.getTerminalSize().getColumns())) {
							coord = viewPos(buffer.getCursor().line - 1, 0);
							int aux = coord[0];
							coord = viewPos(buffer.getCursor().line, 0);
							// Obtem o numero de linhas visuais da linha lógica
							// anterior
							aux = coord[0] - aux - 1;
							int pos = buffer.getNthLine(
									buffer.getCursor().line - 1).length()
									- (aux * (term.getTerminalSize()
											.getColumns()));
							// Verifica se a linha visual atual é maior que a
							// anterior
							if (buffer.getCursor().column > pos)
								al = new Position(buffer.getCursor().line - 1,
										buffer.getNthLine(
												buffer.getCursor().line - 1)
												.length());
							else
								al = new Position(buffer.getCursor().line - 1,
										buffer.getCursor().column
												+ (aux * (term
														.getTerminalSize()
														.getColumns())));
							buffer.setCursor(al);
						} else
							buffer.movePrevLine();

					}
					break;
				case NormalKey:
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					int get_size = buffer.getNthLine(buffer.getCursor().line)
							.length() / term.getTerminalSize().getColumns() - 1;
					int l = coord[0];
					if (buffer.getNthLine(buffer.getCursor().line).length() > term
							.getTerminalSize().getColumns() - 1) {
						if (get_size != term.getTerminalSize().getColumns())
							for (int i = buffer.getCursor().line; i < buffer
									.getLinesCount(); i++)
								modifiedLines.add(i);
					}
					buffer.insertChar(k.getCharacter());
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					// Verifica se mudou de linha visual se sim atualiza todas
					// as outras seguintes
					if (l != coord[0]) {
						if (coord[0] == term.getTerminalSize().getRows()) {
							startRow++;
							for (int i = startRow - 1; i < buffer
									.getLinesCount(); i++)
								modifiedLines.addLast(i);
						} else {
							for (int i = buffer.getCursor().line; i < buffer
									.getLinesCount(); i++)
								modifiedLines.addLast(i);
						}
					} else
						modifiedLines.addLast(new Integer(
								buffer.getCursor().line));
					break;
				case Enter:
					buffer.insertLn();
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					// Verifica se a posição lógica está fora do terminal
					if (coord[0] >= term.getTerminalSize().getRows()) {
						if (buffer.getNthLine(startRow).length() < ((line_height + 1) * term
								.getTerminalSize().getColumns())) {

							startRow++;
							height = 0;
							// Atualiza todas a linhas visuais
							for (int i = startRow; i < buffer.getLinesCount(); i++)
								modifiedLines.addLast(i);
							line_height = 0;

						} else {
							total_line_hight++;
							line_height++;
							String toShow = buffer.getNthLine(startRow)
									.substring(
											line_height
													* term.getTerminalSize()
															.getColumns());
							height = 0;
							show(toShow);
							for (int i = startRow; i < buffer.getLinesCount(); i++)
								modifiedLines.addLast(i);
						}

					} else {
						// atualiza todas as linhas lógicas seguintes
						for (int i = buffer.getCursor().line - 1; i < buffer
								.getLinesCount(); i++)
							modifiedLines.addLast(i);
					}
					break;
				case Backspace:
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					l = coord[0];
					buffer.deleteChar();
					coord = viewPos(buffer.getCursor().line,
							buffer.getCursor().column);
					// Verifica se mudou de linha visual
					if (l != coord[0]) {
						if (l == 0)
							startRow--;
						// Atualiza todas as linhas a seguir a retornada
						for (int i = buffer.getCursor().line; i < buffer
								.getLinesCount(); i++) {
							modifiedLines.addLast(i);
						}
						// limpa a ultima linha
						coord = viewPos(buffer.getLinesCount() - 1, buffer
								.getNthLine(buffer.getLinesCount() - 1)
								.length());
						clearLine(coord[0] + 1);
					} else
						modifiedLines.addLast(new Integer(
								buffer.getCursor().line));
					break;
				}
				// Verifica se houve linhas modificadas caso sim reescreve essas
				// linhas
				if (modifiedLines.size() > 0) {
					term.setCursorVisible(false);
					redraw();
					term.setCursorVisible(true);
				}
				coord = viewPos(buffer.getCursor().line,
						buffer.getCursor().column);
				// Atualiza o cursor
				if (coord != null)
					term.moveCursor(coord[1], coord[0]);
				try {
					Thread.sleep(20);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}
