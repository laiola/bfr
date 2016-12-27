# bfr
BFR(Bradley-Fayyad-Reina) is a variant of k-means designed to handle very large (disk-resident) data sets.

Here's BFR “Galaxies” Picture

![alt text](https://github.com/laiola/bfr/blob/master/src/main/java/resources/%E2%80%9CGalaxies%E2%80%9D%20Picture.PNG "“Galaxies” Picture")

# Three Classes of Points
* Discard set (DS):
  * Points close enough to a centroid to be summarized
* Compression set (CS):
  * Groups of points that are close together but not close to any existing centroid. These points are summarized, but not assigned to a cluster
* Retained set (RS):
  * Isolated points waiting to be assigned to a compression set

# Sufficient Statistic
* The number of points, N
* The vector SUM, whose ith component is the sum of the coordinates of the points in the ith dimension
* The vector SUMSQ: ith component = sum of squares of coordinates in ith dimension

**Average** in each dimension (the centroid) can be calculated as SUMi / N, where SUMi = ith component of SUM.
**Variance** of a cluster’s discard set in dimension i is: (SUMSQi / N) – (SUMi / N)^2. And standard deviation is the square root of that.

# Processing of points 
1. Find those points that are “sufficiently close” to a cluster centroid and add those points to that cluster and the **DS**
  * These points are so close to the centroid that they can be summarized and then discarded
2. Use any main-memory clustering algorithm to cluster the remaining points and the old **RS**
  * Clusters go to the **CS**; outlying points to the **RS**
3. DS set: Adjust statistics of the clusters to account for the new points
  * Add **Ns, SUMs, SUMSQs**
4. Consider merging compressed sets in the **CS**
5. If this is the last round, merge all compressed sets in the **CS** and all **RS** points into their nearest cluster
