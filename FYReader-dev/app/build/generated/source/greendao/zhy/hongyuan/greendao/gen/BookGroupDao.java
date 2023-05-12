package zhy.hongyuan.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import zhy.hongyuan.greendao.entity.BookGroup;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOOK_GROUP".
*/
public class BookGroupDao extends AbstractDao<BookGroup, String> {

    public static final String TABLENAME = "BOOK_GROUP";

    /**
     * Properties of entity BookGroup.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Num = new Property(1, int.class, "num", false, "NUM");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Desc = new Property(3, String.class, "desc", false, "DESC");
    }


    public BookGroupDao(DaoConfig config) {
        super(config);
    }
    
    public BookGroupDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOK_GROUP\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"NUM\" INTEGER NOT NULL ," + // 1: num
                "\"NAME\" TEXT NOT NULL ," + // 2: name
                "\"DESC\" TEXT);"); // 3: desc
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOK_GROUP\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BookGroup entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindLong(2, entity.getNum());
        stmt.bindString(3, entity.getName());
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(4, desc);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BookGroup entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindLong(2, entity.getNum());
        stmt.bindString(3, entity.getName());
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(4, desc);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public BookGroup readEntity(Cursor cursor, int offset) {
        BookGroup entity = new BookGroup( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.getInt(offset + 1), // num
            cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // desc
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BookGroup entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNum(cursor.getInt(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setDesc(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final String updateKeyAfterInsert(BookGroup entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(BookGroup entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BookGroup entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
