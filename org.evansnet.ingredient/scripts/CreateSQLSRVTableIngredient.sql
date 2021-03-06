USE <DBNAME>;

DROP TABLE DBO.INGREDIENT;

CREATE TABLE DBO.INGREDIENT (
    ID BIGINT PRIMARY KEY,
    ING_NAME VARCHAR(40) NOT NULL,
	ING_DESC VARCHAR(80) NULL,
	UNIT_OF_MEASURE BIGINT NULL,
	PKG_UOM BIGINT NULL,
	UNIT_PRICE DECIMAL(6,2) NULL,
	PKG_PRICE DECIMAL (6,2) NULL,
	IS_RECIPE BIT DEFAULT 0);

--SELECT COUNT(*) FROM DBO.INGREDIENT;