<?php
/**
 * Created by PhpStorm.
 * User: Bryant
 * Date: 2017/6/6
 * Time: 11:18
 */
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/Particle.php';
$a = Particle::generate_id_hex();
print($a);