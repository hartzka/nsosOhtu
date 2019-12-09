/*
 * @author londes
 */
package ohtu.commands;

import ohtu.dao.Dao;
import ohtu.domain.Work;
import ohtu.domain.WorkType;
import ohtu.io.IO;

public class ListWorks extends Command {

    public ListWorks(IO io, Dao<Work, Integer> dao) {
        super(io, dao);
    }

    @Override
    public void run() {
        java.util.List<Work> list = dao.list();
        if (list.isEmpty()) {
            io.println("No works yet\n");
        } else {
            io.print("[All]/Read/Unread/Cancel ([A]/R/U/C): ");
            String subList = io.nextLine();
            WorkType type = WorkType.NULL;
            boolean anyType = false;
            io.print("Which category? [Any]/Website/Book/Cancel ([A]/W/B/C): ");
            String typeString = io.nextLine();
            if (typeString.equalsIgnoreCase("W")) {
                type = WorkType.WEBSITE;
            } else if (typeString.equalsIgnoreCase("B")) {
                type = WorkType.BOOK;
            } else if (typeString.equalsIgnoreCase("A") || typeString.isEmpty()) {
                anyType = true;
            } else if (typeString.equalsIgnoreCase("C")) {
                return;
            }
            if (subList.equalsIgnoreCase("A") || subList.isEmpty()) {
                io.println("\nAll works:\n");
                for (Work work : list) {
                    if (anyType || work.getType() == type) {
                        io.println(work + "\n");
                    }
                }
            } else if (subList.equalsIgnoreCase("R")) {
                io.println("\nRead works:\n");
                for (Work work : list) {
                    if (work.getRead()) {
                        if (anyType || work.getType() == type) {
                            io.println(work + "\n");
                        }
                    }
                }
            } else if (subList.equalsIgnoreCase("U")) {
                io.println("\nUnread works:\n");
                for (Work work : list) {
                    if (!work.getRead()) {
                        if (anyType || work.getType() == type) {
                            io.println(work + "\n");
                        }
                    }
                }
            } else if (subList.equalsIgnoreCase("C")) {
                return;
            }
        }
    }

}
