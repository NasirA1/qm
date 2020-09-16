package com.example.dijkstra

import java.lang.StringBuilder
import java.util.*


fun main() {
    // د ستونزې گراف
    val labels = arrayOf("A", "B", "C", "D", "E", "F")
    val graph = arrayOf(
        //              A   B   C   D   E   F
        /* A */ arrayOf(0, 10, 15,  0,  0,  0),
        /* B */ arrayOf(0,  0,  0, 12,  0, 15),
        /* C */ arrayOf(0,  0,  0,  0, 10,  0),
        /* D */ arrayOf(0,  0,  0,  0,  2,  1),
        /* E */ arrayOf(0,  0,  0,  0,  0,  0),
        /* F */ arrayOf(0,  0,  0,  0,  5,  0)
    )
    val src = 0     // د مزل پيل
    val dest = 4    // د مزل منزل

    // د گراف د گاونډيتوب جدول پر پرده باندې ښكاره كول
    printAdjacencyMatrix(labels, graph)

    // د ډايسكسټرا د الگوريديم له لارې لنډترينې لارې موندل
    val (previous, distances) = dijkstra(src, graph)

    // د پيل څخه تر منزله پورې د لنډترينې لارې د مسير جوړول
    val path = makePath(src, dest, labels, previous)

    // پايله پر پرده باندې ښكاره كول
    println("\n د ${labels[src]} څخه ${labels[dest]} ته لنډترينه لاره: ")
    println("$path (${distances[dest]})")
}


private fun printAdjacencyMatrix(labels: Array<String>, graph: Array<Array<Int>>) {
    print("   ")
    labels.forEach { print(it.padStart(3)) }
    println()
    graph.forEachIndexed { idx, row ->
        print(labels[idx].padStart(3))
        row.forEach { col ->
            print("$col".padStart(3))
        }
        println()
    }
}


private fun dijkstra(src: Int, graph: Array<Array<Int>>): Pair<IntArray, IntArray> {

    val distances = IntArray(graph.size) { Int.MAX_VALUE }
    val previous = IntArray(graph.size) { Int.MAX_VALUE }
    val visited = mutableListOf<Int>()

    var currentIndex = src
    distances[currentIndex] = 0

    while(true) {
        val currentNode = graph[currentIndex]

        // د اوسني بند گاونډيان
        val neighbours =
            currentNode.mapIndexed { index, distance ->
                if (distance > 0) index to distance else 0 to 0
            }.filter { it.first > 0 && it.second > 0 }

        // د گاونډيانو د واټنونو شننه
        neighbours.forEach {
            if(distances[currentIndex] + it.second < distances[it.first]) {
                previous[it.first] = currentIndex
                distances[it.first] = distances[previous[it.first]] + it.second
            }
        }

        // د نژدې ترين گاونډي انټخابول
        visited.add(currentIndex)
        val nearest = neighbours.minBy { it.second }
        if(nearest != null) {
            currentIndex = nearest.first
        } else {
            // ختم
            break;
        }
    }

    return previous to distances
}


private fun makePath(src: Int, dest: Int, labels: Array<String>, previous: IntArray): String {
    val st = Stack<Int>()
    var next = dest
    while(next != src) {
        st.push(next)
        next = previous[next]
    }
    st.push(next)

    val sb = StringBuilder()
    while(!st.empty()) {
        sb.append(labels[st.pop()])
        if(st.size > 0) sb.append("->")
    }

    return sb.toString()
}


// د آزموينې لپاره بل گراف
//    val labels = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
//    val graph = arrayOf(
//        //             A   B   C   D   E   F   G   H
//        /*A*/ arrayOf( 0, 20,  0, 80,  0,  0, 90,  0),
//        /*B*/ arrayOf( 0,  0,  0,  0,  0, 10,  0,  0),
//        /*C*/ arrayOf( 0,  0,  0, 10,  0, 50,  0, 20),
//        /*D*/ arrayOf( 0,  0,  0,  0,  0,  0, 20,  0),
//        /*E*/ arrayOf( 0, 50,  0,  0,  0,  0, 30,  0),
//        /*F*/ arrayOf( 0,  0, 10, 40,  0,  0,  0,  0),
//        /*G*/ arrayOf(20,  0,  0,  0,  0,  0,  0,  0),
//        /*H*/ arrayOf( 0,  0,  0,  0,  0,  0,  0,  0)
//    )
//    val src = 0
//    val dest = 7
