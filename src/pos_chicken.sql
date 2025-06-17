-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 17, 2025 at 06:59 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pos_chicken`
--

-- --------------------------------------------------------

--
-- Table structure for table `menu`
--

CREATE TABLE `menu` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `harga` int(11) DEFAULT NULL,
  `kategori` varchar(50) DEFAULT NULL,
  `path_gambar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu`
--

INSERT INTO `menu` (`id`, `nama`, `harga`, `kategori`, `path_gambar`) VALUES
(1, 'ayam', 15000, 'Paket Nasi', ''),
(2, 'ayam', 14000, 'Paket Nasi', ''),
(3, 'ayam goreng', 13000, 'Paket Nasi', '');

-- --------------------------------------------------------

--
-- Table structure for table `tbmenu`
--

CREATE TABLE `tbmenu` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `harga` int(11) DEFAULT NULL,
  `kategori` varchar(50) DEFAULT NULL,
  `gambar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbmenu`
--

INSERT INTO `tbmenu` (`id`, `nama`, `harga`, `kategori`, `gambar`) VALUES
(17, 'Ayam Geprek', 13000, 'Paket Nasi', 'C:\\Users\\oyen\\OneDrive\\Documents\\Tugas SMT 4\\tugas uas pbo 2\\shutterstock_1949306203.jpg'),
(18, 'Ayam Rica Rica', 15000, 'Chicken', 'C:\\Users\\oyen\\OneDrive\\Documents\\Tugas SMT 4\\tugas uas pbo 2\\ayam rica rica.jpg'),
(19, 'Es Jeruk', 5000, 'Minuman', 'C:\\Users\\oyen\\OneDrive\\Pictures\\ES-JERUK-murni.jpg'),
(20, 'Es Teh manis', 3000, 'Minuman', 'C:\\Users\\oyen\\OneDrive\\Pictures\\es teh.jpeg'),
(21, 'Ayam Rica-Rica', 25000, 'Paket Nasi', 'C:\\Users\\oyen\\OneDrive\\Pictures\\Rica+Nasi.jpeg'),
(22, 'Ayam Geprek', 10000, 'Chicken', 'C:\\Users\\oyen\\OneDrive\\Pictures\\download.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `tbuser`
--

CREATE TABLE `tbuser` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('admin','kasir') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbuser`
--

INSERT INTO `tbuser` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', '12345', 'admin'),
(2, 'kasir1', '12345', 'kasir');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `menu`
--
ALTER TABLE `menu`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbmenu`
--
ALTER TABLE `tbmenu`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbuser`
--
ALTER TABLE `tbuser`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `menu`
--
ALTER TABLE `menu`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tbmenu`
--
ALTER TABLE `tbmenu`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `tbuser`
--
ALTER TABLE `tbuser`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
