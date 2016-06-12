/*
 * Copyright (C) 2016 CodeFireUA <edu@codefire.com.ua>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javajdbcswing.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author CodeFireUA <edu@codefire.com.ua>
 */
public class JFrameDAO extends DAO {

    public JFrameDAO(String host, String username, String password) {
        super(host, username, password);
    }

    public TableModel getTableModel(String database, String table) throws SQLException {
        DefaultTableModel defaultTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (getColumnName(column).equalsIgnoreCase("id")) {
                    return false;
                }

                return super.isCellEditable(row, column);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (!getDataVector().isEmpty()) {
                    Vector cells = (Vector) getDataVector().get(0);
                    Object get = cells.get(columnIndex);
                    if (get != null) {
                        return get.getClass();
                    }
                }

                return super.getColumnClass(columnIndex);
            }
        };

        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement().executeQuery(String.format("SELECT * FROM `%s`.`%s`", database, table));

            Vector<String> columns = new Vector<>();
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.add(metaData.getColumnName(i));
            }

//            defaultTableModel.setColumnIdentifiers(columns);
            Vector<Vector> rows = new Vector<>();

            while (rs.next()) {
                Vector<Object> cells = new Vector<>();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    cells.add(rs.getObject(i));
                }

                rows.add(cells);
            }

            defaultTableModel.setDataVector(rows, columns);
        }

        return defaultTableModel;
    }

}
