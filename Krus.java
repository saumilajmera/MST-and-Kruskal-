import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

public class Krus {

	public static void main(String[] args) {

		List<String> records = new ArrayList<String>();
		try {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				if (selectedFile.getName().equalsIgnoreCase("graph.in")){
					InputFile inptFile = new InputFile(selectedFile.getAbsolutePath());
					records = inptFile.ParseFile();
				} else {
					JOptionPane.showMessageDialog(null, "Invalid File Type (It must graph.in)", "MST Algorithm",
							JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
			} else {
				Runtime.getRuntime().exit(0);
			}

		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", "subject");
			e.printStackTrace();
		}

		Prim pm = new Prim(records);
		Graph graph = new Graph(Integer.parseInt(records.get(0)));
		int num = 1;

		HashMap<String, Integer> MapToHoldVertexWithNumber = new HashMap<String, Integer>();
		String names = records.get(1);
		String[] namesList = names.split(",");
		for (String name : namesList) {
			if (name != null && !name.trim().isEmpty()) {
				// attaching unique number to each vertex
				MapToHoldVertexWithNumber.put(name.trim(), num);
				graph.addVertex(num, name.trim().charAt(0));
				num++;
			}
		}

		for (int row = 3; row < records.size(); row++) {
			String[] temp = records.get(row).split("=");
			graph.addEdge(MapToHoldVertexWithNumber.get(temp[0].substring(1, temp[0].length() - 1).split(",")[0]),
					MapToHoldVertexWithNumber.get(temp[0].substring(1, temp[0].length() - 1).split(",")[1]),
					Integer.parseInt(temp[1]));
		}

		graph.applyKrusAlgo();

	}

	public static class Graph {
		Vertex[] vertices;
		Edge edgeList;
		int maxSize;
		int size;
		int edgeNum;

		public Graph(int maxSize) {
			this.maxSize = maxSize;
			vertices = new Vertex[maxSize];
		}

		public class Neighbour {
			int index;
			Neighbour next;
			int weight;

			Neighbour(int index, int weight, Neighbour next) {
				this.index = index;
				this.weight = weight;
				this.next = next;
			}
		}

		public class Vertex {
			int rank;
			Vertex label;
			int name;
			char nameofnode;
			Neighbour adj;

			Vertex(int name, char nodename) {
				this.name = name;
				this.nameofnode = nodename;
				label = this; // make set which makes node as label of itself
			}
		}

		public class Edge {
			Vertex src;
			Vertex dest;
			Edge next;
			int weight;

			Edge(Vertex src, Vertex desti, int weight, Edge next) {
				this.src = src;
				this.dest = desti;
				this.weight = weight;
				this.next = next;
			}
		}

		// Adding vertex to graph
		public void addVertex(int name, char nodename) {
			vertices[size++] = new Vertex(name, nodename);
		}

		// Adding edge to graph
		public void addEdge(int src, int dest, int weight) {
			vertices[src - 1].adj = new Neighbour(dest - 1, weight, vertices[src - 1].adj);
			edgeList = new Edge(vertices[src - 1], vertices[dest - 1], weight, edgeList);
			edgeNum++;
		}

		public void applyKrusAlgo() {
			Edge[] edges = new Edge[edgeNum];
			int iterator = 0;
			while (edgeList != null) {
				edges[iterator] = edgeList;
				iterator++;
				edgeList = edgeList.next;
			}
			quicksort(edges, 0, edgeNum - 1);

			int mstWeight = 0;
			ArrayList<String> listofEdges = new ArrayList<String>();
			for (iterator = 0; iterator < edgeNum; iterator++) {
				Vertex u = findSet(edges[iterator].src);
				Vertex v = findSet(edges[iterator].dest);
				if (u != v) {
					mstWeight++;
					//lexicographically sorted 
					if ((int) (edges[iterator].src.nameofnode) < (int) edges[iterator].dest.nameofnode)
						listofEdges.add(("(" + edges[iterator].src.nameofnode + "," + edges[iterator].dest.nameofnode
								+ ")=" + edges[iterator].weight));
					else
						listofEdges.add(("(" + edges[iterator].dest.nameofnode + "," + edges[iterator].src.nameofnode
								+ ")=" + edges[iterator].weight));
					unionOfVertices(u, v);
				}
			}

			try {
				PrintWriter outputStream = new PrintWriter("Kruskal.out");
				outputStream.println(mstWeight);
				for (int z = 0; z < listofEdges.size(); z++) {
					outputStream.println(listofEdges.get(z));
				}
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Successfully Executed", "MST Algorithm",
					JOptionPane.INFORMATION_MESSAGE);

		}

		// Finding label attached to vertex(recursive call to parent)
		public Vertex findSet(Vertex u) {
			if (u.label != u) {
				u.label = findSet(u.label);
			}
			return u.label;
		}

		// Joining two vertices by making higher rank as parent of lower rank
		public void unionOfVertices(Vertex old, Vertex latest) {
			if (old.rank == latest.rank) {
				latest.label = old;
				old.rank++;
			} else if (old.rank < latest.rank) {
				latest.label = old;
			} else {
				old.label = latest;
			}
		}

		// Function to Sort edges by weight using randomized Quick Sort
		public void quicksort(Edge[] edges, int start, int end) {
			if (start < end) {
				swapping(edges, end, start + (end - start) / 2);
				int partition = pivotPosition(edges, start, end);
				quicksort(edges, start, partition - 1);
				quicksort(edges, partition + 1, end);
			}
		}

		// To locate pivot element position such that values on left and right are
		// sorted
		public int pivotPosition(Edge[] edges, int start, int end) {
			int pIndex = start;
			Edge pivot = edges[end];
			for (int i = start; i < end; i++) {
				if (edges[i].weight < pivot.weight) {
					swapping(edges, i, pIndex);
					pIndex++;
				}
			}
			swapping(edges, end, pIndex);
			return pIndex;
		}

		// Swapping if values are not sorted
		public void swapping(Edge[] edges, int index1, int index2) {
			Edge hold = edges[index1];
			edges[index1] = edges[index2];
			edges[index2] = hold;
		}

	}

	// Class to Validate File
	public static class InputFile {
		String fileName;

		InputFile(String filePath) {
			fileName = filePath;
		}

		public List<String> ParseFile() {
			String line;
			List<String> content = new ArrayList<String>();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(fileName));
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Invalid File Type", "MST Algorithm", JOptionPane.WARNING_MESSAGE);
				System.exit(0);
				e.printStackTrace();
			}
			try {
				while ((line = reader.readLine()) != null) {
					content.add(line);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Invalid File", "MST Algorithm", JOptionPane.WARNING_MESSAGE);
				System.exit(0);
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String message = ValidationFile(content);
			if (!empty(message)) {
				JOptionPane.showMessageDialog(null, message, "MST Algorithm", JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}
			return content;
		}

		// Validation of Input File
		private String ValidationFile(List<String> linesofFiles) {
			String ErrTxt = "";
			List<Character> Nodes = new ArrayList<Character>();
			char name;
			for (int row = 0; row < linesofFiles.size(); row++) {
				// First Line should be numeric
				if (row == 0) {
					if (!isNumeric(linesofFiles.get(row))) {
						ErrTxt = "Invalid Input Condition(Should be numerical value)";
						break;
					}
				}
				// Second Line should have comma separated characters as nodes and must be equal
				// to above mentioned value
				else if (row == 1) {
					String[] namesList = linesofFiles.get(row).split(",");
					if (namesList.length != Integer.parseInt(linesofFiles.get(0))) {
						ErrTxt = "Invalid Input Condition(Mismatch between mentioned number of vertices and present vertices)";
						break;
					}
					for (String item : namesList) {
						name = item.trim().charAt(0);
						int value = (int) name;
						if ((value >= 65 && value <= 90) || (value >= 97 && value <= 122)) {
							Nodes.add(name);
						} else {
							ErrTxt = "Invalid Input Condition(Should be alphabetical character)";
							break;
						}
					}
				}
				// Third Line should be numeric as it contains number of edges
				else if (row == 2) {
					if (!isNumeric(linesofFiles.get(2))) {
						ErrTxt = "Invalid Input Condition(Should be numerical value)";
						break;
					}
				}
				// From fourth line it should have edges with weights and it should be equal to
				// number of edges mentioned above
				else {
					String[] temp = linesofFiles.get(row).split("=");
					if (temp == null) {
						ErrTxt = "Invalid Input Condition";
						break;
					}
					for (char item : temp[0].toCharArray()) {
						if (item == '(' || item == ')' || item == ',')
							continue;
						int value = (int) item;
						if ((value >= 65 && value <= 90) || (value >= 97 && value <= 122)) {
							if (!Nodes.contains(item)) {
								ErrTxt = "Invalid Input Condition(Node in edge is not mentioned as vertex)";
								break;
							}
						} else {
							ErrTxt = "Invalid Input Condition(Should be alphabet character)";
							break;
						}
					}
					if (!isNumeric(temp[1])) {
						ErrTxt = "Invalid Input Condition(Should be numerical value)";
						break;
					}
				}
			}
			return ErrTxt;
		}

		public static boolean isNumeric(String str) {
			try {
				int d = Integer.parseInt(str);
			} catch (NumberFormatException nfe) {
				return false;
			}
			return true;
		}

		public static boolean empty(final String stat) {
			return stat == null || stat.trim().isEmpty();
		}

	}
}
