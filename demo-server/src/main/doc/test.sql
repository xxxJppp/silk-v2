
CREATE TABLE `test` (
  `col1` int(11) DEFAULT NULL,
  `col2` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`col1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- 分表测试,按sid分表
-- 逻辑表
CREATE TABLE `test_sharding_sphere` (
  `sid` int(11) DEFAULT NULL,
  `col2` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- 分表
create table `test_sharding_sphere_0` like `test_sharding_sphere`;
create table `test_sharding_sphere_1` like `test_sharding_sphere`;

