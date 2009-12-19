/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.jdbc.dialect;

import java.sql.SQLException;
import java.util.Collections;

import org.seasar.doma.DomaNullPointerException;
import org.seasar.doma.expr.ExpressionFunctions;
import org.seasar.doma.internal.jdbc.dialect.H2PagingTransformer;
import org.seasar.doma.internal.jdbc.dialect.H2ForUpdateTransformer;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlParameter;
import org.seasar.doma.jdbc.JdbcMappingVisitor;
import org.seasar.doma.jdbc.SelectForUpdateType;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.SqlLogFormattingVisitor;
import org.seasar.doma.jdbc.SqlNode;
import org.seasar.doma.wrapper.Wrapper;

/**
 * H2用の方言です。
 * 
 * @author taedium
 * 
 */
public class H2Dialect extends StandardDialect {

    /** 一意制約違反を表すエラーコード */
    protected static final int UNIQUE_CONSTRAINT_VIOLATION_ERROR_CODE = 23001;

    /**
     * インスタンスを構築します。
     */
    public H2Dialect() {
        this(new H2JdbcMappingVisitor(), new H2SqlLogFormattingVisitor(),
                new H2ExpressionFunctions());
    }

    /**
     * {@link JdbcMappingVisitor} を指定してインスタンスを構築します。
     * 
     * @param jdbcMappingVisitor
     *            {@link Wrapper} をJDBCの型とマッピングするビジター
     */
    public H2Dialect(JdbcMappingVisitor jdbcMappingVisitor) {
        this(jdbcMappingVisitor, new H2SqlLogFormattingVisitor(),
                new H2ExpressionFunctions());
    }

    /**
     * {@link SqlLogFormattingVisitor} を指定してインスタンスを構築します。
     * 
     * @param sqlLogFormattingVisitor
     *            SQLのバインド変数にマッピングされる {@link Wrapper}
     *            をログ用のフォーマットされた文字列へと変換するビジター
     */
    public H2Dialect(SqlLogFormattingVisitor sqlLogFormattingVisitor) {
        this(new H2JdbcMappingVisitor(), sqlLogFormattingVisitor,
                new H2ExpressionFunctions());
    }

    /**
     * {@link ExpressionFunctions} を指定してインスタンスを構築します。
     * 
     * @param expressionFunctions
     *            SQLのコメント式で利用可能な関数群
     */
    public H2Dialect(ExpressionFunctions expressionFunctions) {
        this(new H2JdbcMappingVisitor(), new H2SqlLogFormattingVisitor(),
                expressionFunctions);
    }

    /**
     * {@link JdbcMappingVisitor} と {@link SqlLogFormattingVisitor} と
     * {@link ExpressionFunctions} を指定してインスタンスを構築します。
     * 
     * @param jdbcMappingVisitor
     *            {@link Wrapper} をJDBCの型とマッピングするビジター
     * @param sqlLogFormattingVisitor
     *            SQLのバインド変数にマッピングされる {@link Wrapper}
     *            をログ用のフォーマットされた文字列へと変換するビジター
     * @param expressionFunctions
     *            SQLのコメント式で利用可能な関数群
     */
    public H2Dialect(JdbcMappingVisitor jdbcMappingVisitor,
            SqlLogFormattingVisitor sqlLogFormattingVisitor,
            ExpressionFunctions expressionFunctions) {
        super(jdbcMappingVisitor, sqlLogFormattingVisitor, expressionFunctions);
    }

    @Override
    public String getName() {
        return "h2";
    }

    @Override
    public boolean includesIdentityColumn() {
        return true;
    }

    @Override
    public PreparedSql getIdentitySelectSql(String qualifiedTableName,
            String columnName) {
        if (qualifiedTableName == null) {
            throw new DomaNullPointerException("qualifiedTableName");
        }
        if (columnName == null) {
            throw new DomaNullPointerException("columnName");
        }
        String rawSql = "call identity()";
        return new PreparedSql(SqlKind.SELECT, rawSql, rawSql, null,
                Collections.<PreparedSqlParameter> emptyList());
    }

    @Override
    public PreparedSql getSequenceNextValSql(String qualifiedSequenceName,
            long allocationSize) {
        if (qualifiedSequenceName == null) {
            throw new DomaNullPointerException("qualifiedSequenceName");
        }
        String rawSql = "call next value for " + qualifiedSequenceName;
        return new PreparedSql(SqlKind.SELECT, rawSql, rawSql, null,
                Collections.<PreparedSqlParameter> emptyList());
    }

    @Override
    public boolean isUniqueConstraintViolated(SQLException sqlException) {
        if (sqlException == null) {
            throw new DomaNullPointerException("sqlException");
        }
        int code = getErrorCode(sqlException);
        return UNIQUE_CONSTRAINT_VIOLATION_ERROR_CODE == code;
    }

    @Override
    protected SqlNode toPagingSqlNode(SqlNode sqlNode, long offset, long limit) {
        H2PagingTransformer transformer = new H2PagingTransformer(offset, limit);
        return transformer.transform(sqlNode);
    }

    @Override
    protected SqlNode toForUpdateSqlNode(SqlNode sqlNode,
            SelectForUpdateType forUpdateType, int waitSeconds,
            String... aliases) {
        H2ForUpdateTransformer transformer = new H2ForUpdateTransformer(
                forUpdateType, waitSeconds, aliases);
        return transformer.transform(sqlNode);
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public boolean supportsSequence() {
        return true;
    }

    @Override
    public boolean supportsAutoGeneratedKeys() {
        return true;
    }

    @Override
    public boolean supportsSelectForUpdate(SelectForUpdateType type,
            boolean withTargets) {
        return true;
    }

    /**
     * H2用の {@link JdbcMappingVisitor} の実装です。
     * 
     * @author taedium
     * 
     */
    public static class H2JdbcMappingVisitor extends StandardJdbcMappingVisitor {
    }

    /**
     * H2用の {@link SqlLogFormattingVisitor} の実装です。
     * 
     * @author taedium
     * 
     */
    public static class H2SqlLogFormattingVisitor extends
            StandardSqlLogFormattingVisitor {
    }

    /**
     * H2用の {@link ExpressionFunctions} です。
     * 
     * @author taedium
     * 
     */
    public static class H2ExpressionFunctions extends
            StandardExpressionFunctions {
    }

}
