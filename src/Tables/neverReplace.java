package Tables;

import Tables.TranspositionTable.TableEntry;

public class neverReplace implements IReplaceStrategy{
    /**
     * Ersetzt den alten Wert niemals.
     */
    @Override
    public boolean replace(TableEntry oldEntry, TableEntry newEntry){
        return false;
    }
}
