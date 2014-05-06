package Tables;

import Tables.TranspositionTable.TableEntry;

/**
 * Interface um verschiedene Ersetzungsstrategien fuer verschiedene Hashmaps
 * zu verwenden.
 * 
 */
public interface IReplaceStrategy{
    /**
     * Ersetzungsstrategie fuer die Hashmap. Die Strategie ist
     * implementierungsabhaengig.
     * 
     * @param oldEntry
     *            der evtl. zu ersetzende Wert
     * @param newEntry
     *            der evtl. neue Wert
     * @return true, wenn der alte Eintrag ersetzt werden soll, ansonsten
     *         false
     */
    public boolean replace(TableEntry oldEntry, TableEntry newEntry);
}