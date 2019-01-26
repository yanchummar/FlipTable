-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 26, 2019 at 03:51 PM
-- Server version: 5.7.25-0ubuntu0.18.04.2
-- PHP Version: 7.2.10-0ubuntu0.18.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fliptable`
--
CREATE DATABASE IF NOT EXISTS `fliptable` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `fliptable`;

-- --------------------------------------------------------

--
-- Table structure for table `businesses`
--

CREATE TABLE `businesses` (
  `id` int(255) NOT NULL,
  `mail` varchar(100) NOT NULL,
  `password` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `businesses`
--

INSERT INTO `businesses` (`id`, `mail`, `password`) VALUES
(1, 'yanpooppada@gmail.com', '123');

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservationId` int(255) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `spotId` varchar(400) NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'Pending Confirmation',
  `spotName` varchar(600) NOT NULL,
  `spotLocation` varchar(600) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(240) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `foodieCount` varchar(5) NOT NULL,
  `timeSlot` varchar(10) NOT NULL,
  `bookingDate` varchar(100) NOT NULL,
  `freeBooking` varchar(10) NOT NULL DEFAULT 'true',
  `cost` varchar(100) NOT NULL,
  `tipAmount` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`reservationId`, `hash`, `spotId`, `status`, `spotName`, `spotLocation`, `name`, `email`, `phone`, `foodieCount`, `timeSlot`, `bookingDate`, `freeBooking`, `cost`, `tipAmount`) VALUES
(11, 'k6wrOSj3RJbF2q01j4U2E2s11A34IT', '1', 'Pending Confirmation', 'Tonico Cafe', 'Baker Jn, Kottayam', 'Yan Chummar', 'yanpooppada@gmail.com', '+91 9495678881', '10', '12:30 PM', '1547362804373', 'true', '0', '0');

-- --------------------------------------------------------

--
-- Table structure for table `spots`
--

CREATE TABLE `spots` (
  `city` varchar(60) NOT NULL,
  `trending` varchar(5) NOT NULL DEFAULT 'false',
  `spotId` int(120) NOT NULL,
  `name` varchar(32) NOT NULL,
  `image` varchar(1000) NOT NULL,
  `rating` float NOT NULL,
  `lat` varchar(50) NOT NULL,
  `lng` varchar(50) NOT NULL,
  `location` varchar(50) NOT NULL,
  `cuisines` varchar(100) NOT NULL,
  `priceLevel` int(2) NOT NULL,
  `cost` varchar(20) NOT NULL,
  `openStatus` varchar(5) NOT NULL DEFAULT 'true',
  `openingTime` varchar(7) NOT NULL,
  `closingTime` varchar(7) NOT NULL,
  `phone` varchar(15) NOT NULL DEFAULT '9090909090',
  `address` varchar(180) NOT NULL,
  `imageList` varchar(10000) NOT NULL,
  `amenities` varchar(2000) NOT NULL DEFAULT 'WiFi,Free Parking,Playground',
  `verified` varchar(6) NOT NULL DEFAULT 'true'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `spots`
--

INSERT INTO `spots` (`city`, `trending`, `spotId`, `name`, `image`, `rating`, `lat`, `lng`, `location`, `cuisines`, `priceLevel`, `cost`, `openStatus`, `openingTime`, `closingTime`, `phone`, `address`, `imageList`, `amenities`, `verified`) VALUES
('kottayam', 'true', 1, 'Tonico Cafe', 'https://tastyspots-media.s3.amazonaws.com/cache/9b/e0/9be045c0051e789e124c7cc592dafbb2.jpg', 4.67, '9.5958365', '76.5211212', 'Baker Jn, Kottayam', 'Italian, Continental, Cafe', 4, 'Rs.800 for two', 'true', '11:00', '23:00', '9090909090', 'Josco Midtown Square, Near Nehru Stadium, SH 1, Kottayam, Kerala 686001', 'https://media-cdn.tripadvisor.com/media/photo-s/0e/6e/4b/f8/good-ambience-one-of.jpg,https://tastyspotsmedia.s3.amazonaws.com/cache/4f/c3/4fc31d0ab4dff8edf43b5b54ce5a3659.jpg,https://media-cdn.tripadvisor.com/media/photo-s/0c/39/46/79/belgium-waffles.jpg,https://tastyspotsmedia.s3.amazonaws.com/cache/97/e6/97e69f7c9a8759a9747f171b80736743.jpg,https://b.zmtcdn.com/data/reviews_photos/af2/789c936f5d5d3dd3a71666f575b6faf2_1479463584.jpg', 'WiFi,Free Parking,Playground', 'true'),
('Kottayam', 'true', 2, 'Barbeque-Inn', 'https://s3-ap-southeast-1.amazonaws.com/tastyspotsmedia/foodspots/social-images/tastyspots-social-1000186.jpg', 4.25, '9.5875809', '76.5442381', 'Shastri Rd, Kottayam', 'American, Grilled, Spicy', 2, 'Rs.400 for two', 'true', '12:00', '00:00', '9090909090', 'Kandathil Building, Ground Floor, Opp Skyline Building, Kottayam-Kumily Road, Kanjikuzhi, Kottayam, Kerala 686002', 'https://media-cdn.tripadvisor.com/media/photo-s/0d/af/dd/ea/pakistani-biriyani.jpg,https://media-cdn.tripadvisor.com/media/photo-s/0f/4f/b6/e1/12-to-12-barbeque.jpg,https://media-cdn.tripadvisor.com/media/photo-s/10/3a/47/4a/photo5jpg.jpg,https://tastyspotsmedia.s3.amazonaws.com/cache/59/b7/59b7cf99887361adbf8e290afa0be929.jpg', 'WiFi,Free Parking,Playground', 'true');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userId` int(255) NOT NULL,
  `hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userId`, `hash`) VALUES
(101, 'bE7WfyuWDCUxo4kDU9xFyVUOrjNbzX'),
(99, 'c5eJpghhclKUItmYp5LqpXltQFIybp'),
(97, 'd8bdfinMKFlmZcmeUPFBBAvdQ8pVXi'),
(108, 'fFCTIH6S1iUa8DrO6enKyTAFjfjHY3'),
(102, 'FzXCQJRnnwC7ypjXABlmktsHeMvSa3'),
(94, 'heIsK85U0Rkm7R7qvCN9woFFunmyfA'),
(85, 'IJYCN0AWw3fftF63xoJxLKLUJHiM9c'),
(113, 'k6wrOSj3RJbF2q01j4U2E2s11A34IT'),
(98, 'kaqwoGPLszqNVq0iEU8kvJV1XLamG7'),
(96, 'kTqac4sHjIgR4J1BuLtKKPWZaG0eON'),
(86, 'm3lCI6lQ6pg51lBYKcaMJV2Rs0PSuB'),
(109, 'MN7vIGUwJA4NBVBzBxKzKqU0ZEFIaI'),
(100, 'MVdTd6ofI6OLp6HoS8n0O4tt1nWFbY'),
(105, 'MzZcMVY7Oc6wsyQWOlfIhfJpXGLcxr'),
(92, 'ogwj2xSW4sHmJsgVIM0kbV3TjfIvGX'),
(91, 'orWXvcLgHpnroaQGV9sp6lgVAEougp'),
(88, 'pjr4OuqSt7GGNkQlFH6eq4o2jmIE4W'),
(107, 'pLbuqgG2n8W2fnwvDs6nFRMQ8cDUyb'),
(114, 'reLsURT4yuVCkCkwZbdeMqP4rZrPzS'),
(104, 'rQKk5Dt0XQZECn2S4ESFIYDUxto6oZ'),
(112, 'S2nnvKuhk89ZR9TqlslwmLDfV7NRDZ'),
(84, 'SdFyAHzqRo4mEu75W0fccY91kU9FFl'),
(103, 'sqcM4fvmCZRNPwAN2R3r84Aa2t4coO'),
(95, 'TL7hSv2BIz2ihWYLrZ7FHtFWUsDN2r'),
(106, 'u0YEZ2kbgWp2StfTKdMRgIYQzRJyfi'),
(87, 'VCsAH40xG3qiwKjo3WpKcDr08rUu5n'),
(93, 'x6oIwjyFAGyaLrSHeqtiTAoDI0KHo4'),
(89, 'XawVp1JbwqC9SMplfxKiDvnETAw95H'),
(90, 'xZbDzCmTw8naQZfDWK62rvUj3fgXe4'),
(110, 'yKZNOtnIftlHx6rh7v0zEwR0CDiIBn'),
(111, 'ZXv7seNmKxXOkzJW8ktTUdjPeitT0E');

-- --------------------------------------------------------

--
-- Table structure for table `working_hours`
--

CREATE TABLE `working_hours` (
  `spotId` int(255) NOT NULL,
  `mon` varchar(50) NOT NULL,
  `tue` varchar(50) NOT NULL,
  `wed` varchar(50) NOT NULL,
  `thu` varchar(50) NOT NULL,
  `fri` varchar(50) NOT NULL,
  `sat` varchar(50) NOT NULL,
  `sun` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `working_hours`
--

INSERT INTO `working_hours` (`spotId`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`, `sun`) VALUES
(1, '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00'),
(2, '12:00-00:00', '12:00-00:00', '12:00-00:00', '12:00-00:00', '12:00-00:00', '', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `businesses`
--
ALTER TABLE `businesses`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservationId`),
  ADD UNIQUE KEY `reservationId` (`reservationId`);

--
-- Indexes for table `spots`
--
ALTER TABLE `spots`
  ADD PRIMARY KEY (`spotId`),
  ADD UNIQUE KEY `spotId` (`spotId`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `userId` (`userId`),
  ADD UNIQUE KEY `hash` (`hash`);

--
-- Indexes for table `working_hours`
--
ALTER TABLE `working_hours`
  ADD PRIMARY KEY (`spotId`),
  ADD UNIQUE KEY `spotId` (`spotId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `businesses`
--
ALTER TABLE `businesses`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservationId` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `spots`
--
ALTER TABLE `spots`
  MODIFY `spotId` int(120) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userId` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=115;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
