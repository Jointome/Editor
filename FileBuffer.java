import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.PrintWriter;
import com.googlecode.lanterna.gui.Border.Standard;
import java.util.Scanner;

public class FileBuffer extends Buffer {
	
	private Path savePath;
	// null nao definidio
	private boolean modified;

	// true = modified; false = inalterado
	// gravar
	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(savePath.getFileName().toString(), "UTF-8");

		for(int i = 0 ; i< getLinesCount();i++)
		    writer.println(getNthLine(i));

		writer.close();
	};

	public void saveAS(Path path) throws IOException {
		
		
	};

	public void open(Path path) throws IOException {
		savePath = path;
		BufferedReader buffer = null;
		try {
			String line;
			buffer = new BufferedReader(new FileReader(savePath.getFileName().toString()));
			while ((line = (buffer.readLine())) != null){
				insertStr(line);
				insertLn();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffer != null)
					buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void insertChar(char c) {
		super.insertChar(c);
		modified = true; // marcar modificação
	}
	@Override
	public void insertStr(String s) {
		super.insertStr(s);
		modified = true;
	}
	@Override
	public void insertLn() {
		super.insertLn();
		modified = true;
	}
	@Override
	public void deleteChar() {
		super.deleteChar();
		modified = true;
	}
	@Override
	public void deleteLn() {
		super.deleteLn();
		modified = true;
	}

}
