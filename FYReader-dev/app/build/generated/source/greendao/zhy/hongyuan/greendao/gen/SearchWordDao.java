package zhy.hongyuan.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import java.util.List;
import zhy.hongyuan.greendao.convert.SearchWord1Convert;

import zhy.hongyuan.greendao.entity.search.SearchWord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_WORD".
*/
public class SearchWordDao extends AbstractDao<SearchWord, String> {

    public static final String TABLENAME = "SEARCH_WORD";

    /**
     * Properties of entity SearchWord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property BookId = new Property(0, String.class, "bookId", true, "BOOK_ID");
        public final static Property Keyword = new Property(1, String.class, "keyword", false, "KEYWORD");
        public final static Property SearchWords = new Property(2, String.class, "searchWords", false, "SEARCH_WORDS");
    }

    private final SearchWord1Convert searchWordsConverter = new SearchWord1Convert();

    public SearchWordDao(DaoConfig config) {
        super(config);
    }
    
    public SearchWordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_WORD\" (" + //
                "\"BOOK_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: bookId
                "\"KEYWORD\" TEXT," + // 1: keyword
                "\"SEARCH_WORDS\" TEXT);"); // 2: searchWords
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_WORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchWord entity) {
        stmt.clearBindings();
 
        String bookId = entity.getBookId();
        if (bookId != null) {
            stmt.bindString(1, bookId);
        }
 
        String keyword = entity.getKeyword();
        if (keyword != null) {
            stmt.bindString(2, keyword);
        }
 
        List searchWords = entity.getSearchWords();
        if (searchWords != null) {
            stmt.bindString(3, searchWordsConverter.convertToDatabaseValue(searchWords));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchWord entity) {
        stmt.clearBindings();
 
        String bookId = entity.getBookId();
        if (bookId != null) {
            stmt.bindString(1, bookId);
        }
 
        String keyword = entity.getKeyword();
        if (keyword != null) {
            stmt.bindString(2, keyword);
        }
 
        List searchWords = entity.getSearchWords();
        if (searchWords != null) {
            stmt.bindString(3, searchWordsConverter.convertToDatabaseValue(searchWords));
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public SearchWord readEntity(Cursor cursor, int offset) {
        SearchWord entity = new SearchWord( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // bookId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // keyword
            cursor.isNull(offset + 2) ? null : searchWordsConverter.convertToEntityProperty(cursor.getString(offset + 2)) // searchWords
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchWord entity, int offset) {
        entity.setBookId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setKeyword(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSearchWords(cursor.isNull(offset + 2) ? null : searchWordsConverter.convertToEntityProperty(cursor.getString(offset + 2)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(SearchWord entity, long rowId) {
        return entity.getBookId();
    }
    
    @Override
    public String getKey(SearchWord entity) {
        if(entity != null) {
            return entity.getBookId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SearchWord entity) {
        return entity.getBookId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
