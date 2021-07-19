package ru.noname

import org.apache.spark.sql.functions.{max, sum}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object Solution {
  def main(args: Array[String]): Unit = {

    if(args.length != 2) {
      println("need exact 2 arguments: <inputPath> <outputPath>")
      System.exit(0)
    }
    val inputPath = args(0)
    val outputPath = args(1)

    val spark = SparkSession.builder
      .appName("Solution")
      .getOrCreate()

    val customerSchema = new StructType()
      .add("id", IntegerType, nullable = false)
      .add("name", StringType, nullable = true)
      .add("email", StringType, nullable = true)
      .add("joinDate", DateType, nullable = true)
      .add("status", StringType, nullable = true)
    val customerDF: DataFrame = spark.read
      .options(Map("delimiter" -> "\t"))
      .format("csv")
      .schema(customerSchema)
      .load(s"$inputPath/customer.csv")
    customerDF.createTempView("customers")

    val productSchema = new StructType()
      .add("id", IntegerType, nullable = false)
      .add("name", StringType, nullable = true)
      .add("price", DoubleType, nullable = true)
      .add("numberOfProducts", IntegerType, nullable = true)
    val productDF: DataFrame = spark.read
      .options(Map("delimiter" -> "\t"))
      .format("csv")
      .schema(productSchema)
      .load(s"$inputPath/product.csv")
    productDF.createTempView("products")

    val orderSchema = new StructType()
      .add("customerID", IntegerType, nullable = false)
      .add("orderID", IntegerType, nullable = true)
      .add("productID", IntegerType, nullable = true)
      .add("numberOfProduct", IntegerType, nullable = true)
      .add("orderDate", DateType, nullable = true)
      .add("status", StringType, nullable = true)
    val orderDF: DataFrame = spark.read
      .options(Map("delimiter" -> "\t"))
      .format("csv")
      .schema(orderSchema)
      .load(s"$inputPath/order.csv")

    val sumProductsByIDs = orderDF
      .filter("status = 'delivered'")
      .groupBy("customerID", "productID")
      .agg(sum("numberOfProduct") as "sumProduct")
    sumProductsByIDs.createTempView("orders")

    val maxOrderedByCustomer = sumProductsByIDs
      .groupBy("customerID")
      .agg(max("sumProduct") as "maximum")
    maxOrderedByCustomer.createTempView("max_orders")

    val result = spark.sql(
      """SELECT c.name as customer_name, p.name as product_name
        |FROM orders o
        |INNER JOIN max_orders m ON o.customerID = m.customerID
        |INNER JOIN products p ON o.productID = p.id
        |INNER JOIN customers c ON o.customerID = c.id
        |AND c.status = 'active'
        |AND o.sumProduct = m.maximum""".stripMargin)
    result.coalesce(1).write.mode(SaveMode.Overwrite).csv(s"$outputPath")
  }
}
