package view;

import model.graph.Vertex;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VertexTablePanel<T> extends JPanel {

    private List<Vertex<T>> vertexList;

    public VertexTablePanel(List<Vertex<T>> vertices) {
        this.vertexList = vertices;
        setLayout(new BorderLayout());
        JTable table = createVertexTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JTable createVertexTable() {
        String[] columnNames = {"Place", "X", "Y"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Vertex<T> v : vertexList) {
            Object[] row = {
                    v.getInfo().toString(),
                    String.format("%.2f", v.getX()),
                    String.format("%.2f", v.getY())
            };
            model.addRow(row);
        }

        return new JTable(model);
    }
}