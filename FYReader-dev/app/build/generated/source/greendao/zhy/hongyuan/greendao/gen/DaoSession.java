package zhy.hongyuan.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.BookGroup;
import zhy.hongyuan.greendao.entity.BookMark;
import zhy.hongyuan.greendao.entity.Cache;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.entity.CookieBean;
import zhy.hongyuan.greendao.entity.ReadRecord;
import zhy.hongyuan.greendao.entity.ReplaceRuleBean;
import zhy.hongyuan.greendao.entity.SearchHistory;
import zhy.hongyuan.greendao.entity.SubscribeFile;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.greendao.entity.search.SearchWord;

import zhy.hongyuan.greendao.gen.BookDao;
import zhy.hongyuan.greendao.gen.BookGroupDao;
import zhy.hongyuan.greendao.gen.BookMarkDao;
import zhy.hongyuan.greendao.gen.CacheDao;
import zhy.hongyuan.greendao.gen.ChapterDao;
import zhy.hongyuan.greendao.gen.CookieBeanDao;
import zhy.hongyuan.greendao.gen.ReadRecordDao;
import zhy.hongyuan.greendao.gen.ReplaceRuleBeanDao;
import zhy.hongyuan.greendao.gen.SearchHistoryDao;
import zhy.hongyuan.greendao.gen.SubscribeFileDao;
import zhy.hongyuan.greendao.gen.BookSourceDao;
import zhy.hongyuan.greendao.gen.SearchWordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bookDaoConfig;
    private final DaoConfig bookGroupDaoConfig;
    private final DaoConfig bookMarkDaoConfig;
    private final DaoConfig cacheDaoConfig;
    private final DaoConfig chapterDaoConfig;
    private final DaoConfig cookieBeanDaoConfig;
    private final DaoConfig readRecordDaoConfig;
    private final DaoConfig replaceRuleBeanDaoConfig;
    private final DaoConfig searchHistoryDaoConfig;
    private final DaoConfig subscribeFileDaoConfig;
    private final DaoConfig bookSourceDaoConfig;
    private final DaoConfig searchWordDaoConfig;

    private final BookDao bookDao;
    private final BookGroupDao bookGroupDao;
    private final BookMarkDao bookMarkDao;
    private final CacheDao cacheDao;
    private final ChapterDao chapterDao;
    private final CookieBeanDao cookieBeanDao;
    private final ReadRecordDao readRecordDao;
    private final ReplaceRuleBeanDao replaceRuleBeanDao;
    private final SearchHistoryDao searchHistoryDao;
    private final SubscribeFileDao subscribeFileDao;
    private final BookSourceDao bookSourceDao;
    private final SearchWordDao searchWordDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bookDaoConfig = daoConfigMap.get(BookDao.class).clone();
        bookDaoConfig.initIdentityScope(type);

        bookGroupDaoConfig = daoConfigMap.get(BookGroupDao.class).clone();
        bookGroupDaoConfig.initIdentityScope(type);

        bookMarkDaoConfig = daoConfigMap.get(BookMarkDao.class).clone();
        bookMarkDaoConfig.initIdentityScope(type);

        cacheDaoConfig = daoConfigMap.get(CacheDao.class).clone();
        cacheDaoConfig.initIdentityScope(type);

        chapterDaoConfig = daoConfigMap.get(ChapterDao.class).clone();
        chapterDaoConfig.initIdentityScope(type);

        cookieBeanDaoConfig = daoConfigMap.get(CookieBeanDao.class).clone();
        cookieBeanDaoConfig.initIdentityScope(type);

        readRecordDaoConfig = daoConfigMap.get(ReadRecordDao.class).clone();
        readRecordDaoConfig.initIdentityScope(type);

        replaceRuleBeanDaoConfig = daoConfigMap.get(ReplaceRuleBeanDao.class).clone();
        replaceRuleBeanDaoConfig.initIdentityScope(type);

        searchHistoryDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchHistoryDaoConfig.initIdentityScope(type);

        subscribeFileDaoConfig = daoConfigMap.get(SubscribeFileDao.class).clone();
        subscribeFileDaoConfig.initIdentityScope(type);

        bookSourceDaoConfig = daoConfigMap.get(BookSourceDao.class).clone();
        bookSourceDaoConfig.initIdentityScope(type);

        searchWordDaoConfig = daoConfigMap.get(SearchWordDao.class).clone();
        searchWordDaoConfig.initIdentityScope(type);

        bookDao = new BookDao(bookDaoConfig, this);
        bookGroupDao = new BookGroupDao(bookGroupDaoConfig, this);
        bookMarkDao = new BookMarkDao(bookMarkDaoConfig, this);
        cacheDao = new CacheDao(cacheDaoConfig, this);
        chapterDao = new ChapterDao(chapterDaoConfig, this);
        cookieBeanDao = new CookieBeanDao(cookieBeanDaoConfig, this);
        readRecordDao = new ReadRecordDao(readRecordDaoConfig, this);
        replaceRuleBeanDao = new ReplaceRuleBeanDao(replaceRuleBeanDaoConfig, this);
        searchHistoryDao = new SearchHistoryDao(searchHistoryDaoConfig, this);
        subscribeFileDao = new SubscribeFileDao(subscribeFileDaoConfig, this);
        bookSourceDao = new BookSourceDao(bookSourceDaoConfig, this);
        searchWordDao = new SearchWordDao(searchWordDaoConfig, this);

        registerDao(Book.class, bookDao);
        registerDao(BookGroup.class, bookGroupDao);
        registerDao(BookMark.class, bookMarkDao);
        registerDao(Cache.class, cacheDao);
        registerDao(Chapter.class, chapterDao);
        registerDao(CookieBean.class, cookieBeanDao);
        registerDao(ReadRecord.class, readRecordDao);
        registerDao(ReplaceRuleBean.class, replaceRuleBeanDao);
        registerDao(SearchHistory.class, searchHistoryDao);
        registerDao(SubscribeFile.class, subscribeFileDao);
        registerDao(BookSource.class, bookSourceDao);
        registerDao(SearchWord.class, searchWordDao);
    }
    
    public void clear() {
        bookDaoConfig.clearIdentityScope();
        bookGroupDaoConfig.clearIdentityScope();
        bookMarkDaoConfig.clearIdentityScope();
        cacheDaoConfig.clearIdentityScope();
        chapterDaoConfig.clearIdentityScope();
        cookieBeanDaoConfig.clearIdentityScope();
        readRecordDaoConfig.clearIdentityScope();
        replaceRuleBeanDaoConfig.clearIdentityScope();
        searchHistoryDaoConfig.clearIdentityScope();
        subscribeFileDaoConfig.clearIdentityScope();
        bookSourceDaoConfig.clearIdentityScope();
        searchWordDaoConfig.clearIdentityScope();
    }

    public BookDao getBookDao() {
        return bookDao;
    }

    public BookGroupDao getBookGroupDao() {
        return bookGroupDao;
    }

    public BookMarkDao getBookMarkDao() {
        return bookMarkDao;
    }

    public CacheDao getCacheDao() {
        return cacheDao;
    }

    public ChapterDao getChapterDao() {
        return chapterDao;
    }

    public CookieBeanDao getCookieBeanDao() {
        return cookieBeanDao;
    }

    public ReadRecordDao getReadRecordDao() {
        return readRecordDao;
    }

    public ReplaceRuleBeanDao getReplaceRuleBeanDao() {
        return replaceRuleBeanDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    public SubscribeFileDao getSubscribeFileDao() {
        return subscribeFileDao;
    }

    public BookSourceDao getBookSourceDao() {
        return bookSourceDao;
    }

    public SearchWordDao getSearchWordDao() {
        return searchWordDao;
    }

}
