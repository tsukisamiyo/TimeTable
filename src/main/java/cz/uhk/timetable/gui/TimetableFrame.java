package cz.uhk.timetable.gui;

import cz.uhk.timetable.model.LocationTimetable;
import cz.uhk.timetable.model.RoomData;
import cz.uhk.timetable.utils.ITimetableProvider;
import cz.uhk.timetable.utils.StagRoomRequestBuilder;
import cz.uhk.timetable.utils.StagTimetableProvider;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class TimetableFrame extends JFrame {
    private ITimetableProvider timetableProvider;
    private LocationTimetable timetable;
    private JTable table;
    private TimetableModel timetableModel;

    public TimetableFrame() {
        super("Location timetable");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        timetableProvider = new StagTimetableProvider();
        initGui();
    }

    private void initGui() {
        timetableModel = new TimetableModel();
        table = new JTable(timetableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(getToolBar(), BorderLayout.NORTH);
        setPreferredSize(new Dimension(900, 800));
        pack();
    }

    private JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);

        JComboBox<String> buildingCombo = new JComboBox<>();
        JComboBox<String> roomCombo = new JComboBox<>();

        Comparator<String> lettersFirst = (a, b) -> {
            boolean aLetter = !a.isEmpty() && Character.isLetter(a.charAt(0));
            boolean bLetter = !b.isEmpty() && Character.isLetter(b.charAt(0));
            if (aLetter != bLetter) return aLetter ? -1 : 1;
            return a.compareTo(b);
        };

        List<RoomData> allRooms = new StagRoomRequestBuilder().execute();
        List<String> buildings = allRooms.stream()
                .map(RoomData::getBuildingCode)
                .filter(b -> b != null && !b.isBlank())
                .distinct()
                .sorted(lettersFirst)
                .toList();

        buildingCombo.addItem("");
        buildings.forEach(buildingCombo::addItem);

        buildingCombo.addActionListener(e -> {
            String selected = (String) buildingCombo.getSelectedItem();
            roomCombo.removeAllItems();
            if (selected == null || selected.isBlank()) {
                return;
            }
            List<RoomData> rooms = new StagRoomRequestBuilder()
                    .filterByBuildingCode(selected)
                    .execute();
            roomCombo.addItem("");
            rooms.stream()
                    .map(RoomData::getRoomCode)
                    .filter(r -> r != null && !r.isBlank())
                    .distinct()
                    .sorted(lettersFirst)
                    .forEach(roomCombo::addItem);
        });

        roomCombo.addActionListener(e -> {
            String building = (String) buildingCombo.getSelectedItem();
            String room = (String) roomCombo.getSelectedItem();
            if (building == null || building.isBlank() || room == null || room.isBlank()) {
                return;
            }
            timetable = timetableProvider.readTimetable(building, room);
            timetableModel.fireTableDataChanged();
        });

        toolBar.add(new JLabel("Budova: "));
        toolBar.add(buildingCombo);
        toolBar.addSeparator();
        toolBar.add(new JLabel("Mistnost: "));
        toolBar.add(roomCombo);
        return toolBar;
    }

    class TimetableModel extends AbstractTableModel {

        private static final String[] COLNAMES = {
                "ZKRATKA", "NAZEV", "UCITEL", "TYP", "DEN", "ZACATEK", "KONEC"
        };

        @Override
        public int getRowCount() {
            return timetable == null ? 0 : timetable.getActivities().size();
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public String getColumnName(int column) {
            return COLNAMES[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var a = timetable.getActivities().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> a.getId();
                case 1 -> a.getName();
                case 2 -> a.getTeacher();
                case 3 -> a.getType();
                case 4 -> a.getDay();
                case 5 -> a.getStart();
                case 6 -> a.getEnd();
                default -> null;
            };
        }
    }
}
