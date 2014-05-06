package Tables;

import Tables.TranspositionTable.TableEntry;

public class pvNodePriority implements IReplaceStrategy{

    @Override
    public boolean replace(TableEntry oldEntry, TableEntry newEntry){
        if(oldEntry.countofmoves < newEntry.countofmoves
                || !oldEntry.isExact){
            return true;
        }
        if(newEntry.isPvnode){
            return true;
        }
        if(!oldEntry.isPvnode && newEntry.isExact){
            return true;
        }
        else{
            return false;
        }
    }

}