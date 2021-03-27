package com.micro.secureapp.crypt;

public class DocumentCryptException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2880331895556990933L;
	Object id;
    String collectionName;

    public DocumentCryptException(String collectionName, Object id, Throwable e) {
        super("Collection: " + collectionName + ", Id: " + id, e);
        this.id = id;
        this.collectionName = collectionName;
    }

    public Object getId() {
        return id;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
