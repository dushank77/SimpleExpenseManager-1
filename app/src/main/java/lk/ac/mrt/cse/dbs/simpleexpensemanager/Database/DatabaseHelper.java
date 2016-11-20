package lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Dushan on 2016-11-20.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String DATABASE_NAME = "140323f.db";

    //account table
    private static final String ACCOUNT_TABLE_NAME = "account_table";

    private static final String A_COL_1_ID = "id";
    private static final String A_COL_2_ACCOUNT_NUMBER = "account_number";
    private static final String A_COL_3_NAME = "name";
    private static final String A_COL_4_BANK = "bank";
    private static final String A_COL_5_BALANCE = "balance";

    private static final String CREATE_ACCOUNT_TABLE = "create table " + ACCOUNT_TABLE_NAME + " (" + A_COL_1_ID + " integer primary key autoincrement,"
            + A_COL_2_ACCOUNT_NUMBER + " text,"
            + A_COL_3_NAME + " text,"
            + A_COL_4_BANK + " text"
            + A_COL_5_BALANCE + " double)";
    //

    //transaction table
    private static final String TRANSACTION_TABLE_NAME = "transaction_table";

    private static final String T_COL_1_ID = "id";
    private static final String T_COL_2_ACCOUNT_NUMBER = "account_number";
    private static final String T_COL_3_TRANSACTION_TYPE = "type";
    private static final String T_COL_4_AMOUNT = "amount";
    private static final String T_COL_5_DATE = "date";

    private static final String CREATE_TRANSACTION_TABLE = "create table " + TRANSACTION_TABLE_NAME + " (" + T_COL_1_ID + " integer primary key autoincrement,"
            + T_COL_2_ACCOUNT_NUMBER + " text,"
            + T_COL_3_TRANSACTION_TYPE + " text,"
            + T_COL_4_AMOUNT + " double,"
            + T_COL_5_DATE + " date)";
    //

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        db= getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist "+ACCOUNT_TABLE_NAME);
        db.execSQL("drop table if exist "+TRANSACTION_TABLE_NAME);
        onCreate(db);

    }

    public void insertDataToAccountTable(Account account) {
        if(account!=null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValuesA = new ContentValues();
            contentValuesA.put(A_COL_2_ACCOUNT_NUMBER, account.getAccountNo());
            contentValuesA.put(A_COL_3_NAME, account.getAccountHolderName());
            contentValuesA.put(A_COL_4_BANK, account.getBankName());
            contentValuesA.put(A_COL_5_BALANCE, account.getBalance());

            long result = db.insert(ACCOUNT_TABLE_NAME, null, contentValuesA);
//            if (result == -1) return false;
//            else return true;
        }
//        return false;
    }

    public boolean insertDataToTransactionTable(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValuesT = new ContentValues();
        contentValuesT.put(T_COL_2_ACCOUNT_NUMBER, transaction.getAccountNo());
        contentValuesT.put(T_COL_3_TRANSACTION_TYPE, transaction.getExpenseType().toString());
        contentValuesT.put(T_COL_4_AMOUNT, transaction.getAmount());
        contentValuesT.put(T_COL_5_DATE, String.valueOf(transaction.getDate()));


        long result = db.insert(TRANSACTION_TABLE_NAME, null, contentValuesT);
        if (result == -1) return false;
        else return true;
    }

    private Cursor getAllAccountCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + ACCOUNT_TABLE_NAME, null);
        return res;
    }

    private Cursor getAllTransactionCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TRANSACTION_TABLE_NAME, null);
        return res;
    }

    private Account cursorToAccount(Cursor cursor) {

        Account newAccount = new Account(cursor.getString(1), cursor.getString(3), cursor.getString(2), cursor.getDouble(4));
        return newAccount;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        Transaction newTransaction = new Transaction(new Date(cursor.getString(4)), cursor.getString(1), ExpenseType.valueOf(cursor.getString(2)), cursor.getDouble(3));
        return newTransaction;
    }

    private Cursor getAccountNumbersCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + A_COL_2_ACCOUNT_NUMBER + " from " + ACCOUNT_TABLE_NAME, null);
        return res;
    }

    public ArrayList<String> getAccountNumbers() {
        ArrayList<String> accountnumList = new ArrayList<>();
//        Cursor cur = getAccountNumbersCursor();
//        while (cur.moveToNext()) {
//            accountnumList.add(cur.getString(0));
//        }
        for(Account a: getAllAccounts()){
            accountnumList.add(a.getAccountNo());
        }

        return accountnumList;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> accountList = new ArrayList<>();
        Cursor cur = getAllAccountCursor();
        while (cur.moveToNext()) {
            accountList.add(cursorToAccount(cur));
        }
        return accountList;
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactionList = new ArrayList<>();
        Cursor cur = getAllTransactionCursor();
        while (cur.moveToNext()) {
            transactionList.add(cursorToTransaction(cur));
        }
        return transactionList;
    }

    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + ACCOUNT_TABLE_NAME + " where " + A_COL_2_ACCOUNT_NUMBER + " =" + accountNo, null);
        return cursorToAccount(res);
    }

    public boolean updateAccountBalance(String accountNo, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(A_COL_5_BALANCE, balance);
        db.update(ACCOUNT_TABLE_NAME, contentValues, A_COL_2_ACCOUNT_NUMBER + " = ?", new String[]{accountNo});
        return true;
    }


}
