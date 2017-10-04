/*
 * Copyright (c) 2015 Samelamin, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samelamin.spark.bigquery

import com.google.cloud.hadoop.io.bigquery.BigQueryStrings
import com.samelamin.spark.bigquery.converters.SchemaConverters
import com.samelamin.spark.bigquery.streaming.{BigQuerySink, BigQuerySource}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.execution.streaming.{Sink, Source}
import org.apache.spark.sql.sources._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.StructType
/**
  * The default BigQuery source for Spark SQL.
  */
class DefaultSource extends RelationProvider /*with SchemaRelationProvider*/ {

  private def getConvertedSchema(sqlContext: SQLContext,options: Map[String, String]): StructType = {
    val bigqueryClient = BigQueryClient.getInstance(sqlContext)
    val tableReference = BigQueryStrings.parseTableReference(options.get("tableReferenceSource").get)
    SchemaConverters.BQToSQLSchema(bigqueryClient.getTableSchema(tableReference))
  }

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    val otherSqlName = sqlContext
    new BaseRelation {
      override def schema: StructType = getConvertedSchema(sqlContext, parameters)
      override def sqlContext: SQLContext = otherSqlName
    }
  }

  /*
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {
    val otherSqlName = sqlContext
    val otherSchemaName = schema
    new BaseRelation {
    override def schema: StructType = otherSchemaName
    override def sqlContext: SQLContext = otherSqlName
    }
  }
  */
}
