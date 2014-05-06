package Tables;

import Tables.TranspositionTable.TableEntry;

public class alwaysReplace implements IReplaceStrategy{
    /**
     * Ersetzt den alten Wert immer.
     */
    @Override
    public boolean replace(TableEntry oldEntry, TableEntry newEntry){
        return true;
    }

}