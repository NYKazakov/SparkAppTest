package ru.noname

import org.apache.spark.sql.functions.{max, sum}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object Solution {
  def main(args: Array[String]): Unit = {

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
      .load("resources/customer.csv")
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
      .load("resources/product.csv")
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
      .load("resources/order.csv")

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
        |AND sum(o.numberOfProduct) = m.maximum""".stripMargin)
    result.write.csv("result")
  }
}
