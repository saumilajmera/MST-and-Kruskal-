import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Prim {

	public Prim(List<String> listFileLines) {
		Graph graph = new Graph(Integer.parseInt(listFileLines.get(0)));

		String names = listFileLines.get(1);
		String[] namesList = names.split(",");
		for (String name : namesList) {
			if (name != null && !name.trim().isEmpty())
				graph.addNode(name.trim().charAt(0));
		}

		for (int row = 3; row < listFileLines.size(); row++) {
			String[] temp = listFileLines.get(row).split("=");
			if (temp != null && temp.length == 2 && temp[0].trim() != null && temp[1].trim() != null)
				graph.addEdge(temp[0].trim().charAt(1), temp[0].trim().charAt(3), Integer.parseInt(temp[1].trim()));
		}
		graph.PrimAlgo();
	}

	public static class Graph {
		private Vertex verticies[];
		private int maxSize;
		private int size;
		private HashMap map;
		private MinHeap Queue;

		public Graph(int maxSize) {
			this.maxSize = maxSize;
			verticies = new Vertex[maxSize];
			map = new HashMap(maxSize);
			Queue = new MinHeap(maxSize);
		}

		// add node to graph
		public void addNode(char data) {
			verticies[size] = new Vertex(data, size);
			map.put(data, size);
			size++;
		}

		// add edge to graph
		public void addEdge(char sourceData, char destinationData, int weight) {
			int sourceIndex = map.get(sourceData);
			int destinationIndex = map.get(destinationData);
			verticies[sourceIndex].adj = new Neighbour(destinationIndex, weight, verticies[sourceIndex].adj);
			verticies[destinationIndex].adj = new Neighbour(sourceIndex, weight, verticies[destinationIndex].adj);
		}

		public void PrimAlgo() {

			PrimEdge mstPe = null;
			Vertex vertex = verticies[0];
			List<PrimEdge> finalMST = new ArrayList<PrimEdge>();
			vertex.cost = 0;
			vertex.state = Vertex.IN_QUEUE;
			Queue.add(vertex);
			while (!Queue.isEmpty()) {
				Vertex poppedVertex = Queue.remove();
				poppedVertex.state = Vertex.NODEVISITED;

				Neighbour temp = poppedVertex.adj;
				if (poppedVertex.parentIndex != -1) {
					char source = verticies[poppedVertex.index].data;
					char destination = verticies[poppedVertex.parentIndex].data;
					mstPe = new PrimEdge(source, destination, mstPe, poppedVertex.cost);
					finalMST.add(mstPe);
				}
				while (temp != null) {
					Vertex adjVertex = verticies[temp.index];
					if (adjVertex.state != Vertex.NODEVISITED) {
						if (adjVertex.cost > temp.weight) {
							adjVertex.cost = temp.weight;
							adjVertex.parentIndex = poppedVertex.index;
						}
						if (adjVertex.state != Vertex.IN_QUEUE) {
							Queue.add(adjVertex);
							adjVertex.state = Vertex.IN_QUEUE;
						} else {
							// extract up this Node in the heap
							Queue.moveUp(adjVertex);
						}
					}
					temp = temp.next;
				}
			}

			List<String> listofEdges = new ArrayList<String>();			
			int mstEdges = 0;
			if (finalMST != null && finalMST.size() > 0) {
				for (int location = 0; location < finalMST.size(); location++) {
					
					//lexicographically sorted
					if ((int) finalMST.get(location).source <= (int) finalMST.get(location).destination)
						listofEdges.add("(" + finalMST.get(location).source + "," + finalMST.get(location).destination
								+ ")=" + finalMST.get(location).mstweight);
					else
						listofEdges.add("(" + finalMST.get(location).destination + "," + finalMST.get(location).source
								+ ")=" + finalMST.get(location).mstweight);
					mstEdges++;
				}
			}
			
			try {
				PrintWriter outputStream = new PrintWriter("Prim.out");
				outputStream.println(mstEdges);
				for (int z = 0; z < listofEdges.size(); z++) {
					outputStream.println(listofEdges.get(z));
				}
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Class to hold Prim classified edges
		private static class PrimEdge {
			public char source;
			public char destination;
			public int mstweight;
			private PrimEdge next;

			public PrimEdge(char source, char destination, PrimEdge next, int mstweight) {
				this.source = source;
				this.destination = destination;
				this.next = next;
				this.mstweight = mstweight;
			}
		}

		// Binary Heap
		public static class MinHeap {
			private Vertex[] items;
			private int maxSize;
			private int size;

			public MinHeap(int maxSize) {
				this.maxSize = maxSize;
				items = new Vertex[maxSize];
			}

			public void moveUp(Vertex vertex) {
				int iterator = 0;
				for (; iterator < size; iterator++) {
					if (items[iterator] == vertex) {
						break;
					}
				}
				if (iterator < size) {
					int currentIndex = iterator;
					Vertex currentItem = items[currentIndex];
					int parentIndex = (currentIndex - 1) / 2;
					Vertex parentItem = items[parentIndex];
					while (currentItem.compareTo(parentItem) == -1) {
						swap(currentIndex, parentIndex);
						currentIndex = parentIndex;
						currentItem = items[currentIndex];
						parentIndex = (currentIndex - 1) / 2;
						parentItem = items[parentIndex];
					}
				}
			}

			public void add(Vertex item) {
				items[size] = item;
				heapifyAfterAdd();
				size++;
			}

			private void swap(int position1, int position2) {
				Vertex temp = items[position1];
				items[position1] = items[position2];
				items[position2] = temp;
			}

			private void heapifyAfterAdd() {
				int pIndex = size;
				Vertex currItem = items[pIndex];
				int parentIndex = pIndex / 2;
				Vertex parentItem = items[parentIndex];
				while (currItem.compareTo(parentItem) == -1) {
					swap(parentIndex, pIndex);
					pIndex = parentIndex;
					currItem = items[pIndex];
					parentIndex = pIndex / 2;
					parentItem = items[parentIndex];
				}
			}

			public Vertex remove() {
				return remove(0);
			}

			public Vertex remove(Vertex vertex) {
				int iterator = 0;
				for (; iterator < size; iterator++) {
					if (items[iterator] == vertex) {
						break;
					}
				}
				if (iterator < size) {
					return remove(iterator);
				}
				return null;

			}

			private Vertex remove(int index) {
				Vertex vertex = items[index];
				swap(index, size - 1);
				items[size - 1] = null;
				size--;
				heapifyAfterRemoval(index);
				return vertex;
			}

			private void heapifyAfterRemoval(int index) {
				int currIndex = index;
				Vertex currItem = items[currIndex];
				int childIndex;
				Vertex childItem;
				int left = 2 * currIndex + 1;
				int right = 2 * currIndex + 2;
				if (left > size - 1) {
					return;
				}
				if (right > size - 1) {
					childIndex = left;
				} else if (items[left].compareTo(items[right]) == -1) {
					childIndex = left;
				} else {
					childIndex = right;
				}
				childItem = items[childIndex];

				while (childItem.compareTo(currItem) == -1) {
					swap(currIndex, childIndex);
					currIndex = childIndex;
					currItem = items[currIndex];
					left = 2 * currIndex + 1;
					right = 2 * currIndex + 2;
					if (left > size - 1) {
						return;
					}
					if (right > size - 1) {
						childIndex = left;
					} else if (items[left].compareTo(items[right]) == -1) {
						childIndex = left;
					} else {
						childIndex = right;
					}
					childItem = items[childIndex];
				}
			}

			public boolean isEmpty() {
				return size == 0;
			}
		}

		public static class HashMap {
			private MapNode[] map;
			private char[] keySet;
			private int maxSize;
			private int size;

			public HashMap(int maxSize) {
				this.maxSize = maxSize;
				map = new MapNode[maxSize];
				keySet = new char[maxSize];
			}

			private static class MapNode {
				char key;
				int value;
				MapNode next;

				public MapNode(char key, int value, MapNode next) {
					this.key = key;
					this.value = value;
					this.next = next;
				}
			}

			public int hash(char key) {
				return 31 * key;
			}

			public int getmapIndexOfkey(char key) {
				return hash(key) % maxSize;
			}

			public void put(char key, int value) {
				int index = getmapIndexOfkey(key);
				map[index] = new MapNode(key, value, map[index]);
				keySet[index] = key;
				size++;
			}

			public int get(char key) {
				int index = getmapIndexOfkey(key);
				MapNode temp = map[index];
				while (temp != null) {
					if (temp.key == key) {
						break;
					}
				}
				if (temp != null) {
					return temp.value;
				} else {
					return -1;
				}
			}

			public char[] keyset() {
				return keySet;
			}
		}

		public static class Vertex {
			public static final int NEWNODE = 0;
			public static final int IN_QUEUE = 1;
			public static final int NODEVISITED = 2;
			private int state = NEWNODE;
			private int cost = Integer.MAX_VALUE;
			private char data;
			private Neighbour adj;
			private int index;
			private int parentIndex = -1;

			public int compareTo(Vertex passed) {
				if (cost < passed.cost) {
					return -1;
				}
				if (cost > passed.cost) {
					return 1;
				}
				return 0;
			}

			public Vertex(char data, int index) {
				this.data = data;
				this.index = index;
			}

			public void addAdjacentVertex(Neighbour adj) {
				this.adj = adj;
			}

			public void updateWeight(int newCost, int parentIndex) {
				this.cost = newCost;
				this.parentIndex = parentIndex;
			}
		}

		public static class Neighbour {
			private Neighbour next;
			private int index;
			private int weight;

			public Neighbour(int index, int weight, Neighbour next) {
				this.next = next;
				this.index = index;
				this.weight = weight;
			}
		}
	}
}
