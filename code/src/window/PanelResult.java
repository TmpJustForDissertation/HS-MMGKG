package window;

import java.awt.BorderLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.Result;
public class PanelResult extends JPanel{
	private static final long serialVersionUID = 1L;
	private JTextArea jta=null;
	private HashSet<Result> results=null;
	private Main main=null;
	public PanelResult(Main main) {
		// TODO Auto-generated constructor stub
		this.main=main;
		results=new HashSet<Result>();
		this.setLayout(new BorderLayout());
		jta=new JTextArea();
		JScrollPane jsp = new JScrollPane(jta);
		this.add(jsp,BorderLayout.CENTER);
		jta.setEditable(false);
	}


	public final HashSet<Result> addResult(Result result) {
		// TODO Auto-generated method stub
		Iterator<Result> it=results.iterator();
		HashSet<Result> s=new HashSet<Result>();
		while(it.hasNext()){
			Result r=it.next();
			if(r.contains(result)){
				s.add(r);
			}
		}
		it=s.iterator();
		while(it.hasNext()){
			results.remove(it.next());
		}
		results.add(result);
		it=results.iterator();
		jta.setText("");
		while(it.hasNext()){
			jta.append(it.next().toString2());
		}
		return results;
	}


	public final void exportResult() {
		// TODO Auto-generated method stub
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("select the file to save the result");
		chooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("HS_MMGK result file(*.sly)","sly");
		chooser.setFileFilter(filter);
		if(JFileChooser.APPROVE_OPTION==chooser.showOpenDialog(main)){
			String filename=chooser.getSelectedFile().getAbsolutePath();
			try{
				save(filename);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.out.println("no file has been selected");
		}
	}
	private final void save(String filename) throws IOException{
		FileOutputStream fos=new FileOutputStream(filename);
		Iterator<Result> it=results.iterator();
		while(it.hasNext()){
			fos.write("####\n".getBytes());
			Result result=it.next();
			fos.write(result.toString2().getBytes());
			ArrayList<Result> al=Result.getResults(result, main.geno);
			Iterator<Result> iti=al.iterator();
			while(iti.hasNext()){
				fos.write(iti.next().toString2().getBytes());
			}
			fos.write("####\n".getBytes());
		}
		fos.close();
	}


	public final void clear() {
		// TODO Auto-generated method stub
		results.clear();
		jta.setText("");
	}
}
