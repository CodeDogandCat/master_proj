<?php

/**
 * Class Particle   Twitter snowflake 算法，分配全局唯一ID，可以用到2082年
 */
class Particle
{
    const EPOCH = 1479533469598;
    const max4bit = 15;
    const max41bit = 1099511627775;

    static $machineId = null;

    public static function machineId($mId)
    {
        self::$machineId = $mId;
    }

    public static function generateParticle()
    {
        /*
        * Time - 42 bits
        */
        $time = floor(microtime(true) * 1000);

        /*
        * Substract custom epoch from current time
        */
        $time -= self::EPOCH;

        /*
        * Create a base and add time to it
        */
        $base = decbin(self::max41bit + $time);

        /*
        * Configured machine id - 3 bits - up to 8 machines
        */
        $machineid = str_pad(decbin(self::$machineId), 3, "0", STR_PAD_LEFT);

        /*
        * sequence number - 4 bits - up to 16 random numbers per machine
        */
        $random = str_pad(decbin(mt_rand(0, self::max4bit)), 4, "0", STR_PAD_LEFT);

        /*
        * Pack
        */
        $base = $base . $machineid . $random;

        /*
        * Return unique time id no
        */
        return bindec($base);
    }

    public static function timeFromParticle($particle)
    {
        /*
        * Return time
        */
        return bindec(substr(decbin($particle), 0, 41)) - self::max41bit + self::EPOCH;
    }

    //http://luokr.com/p/16
    public static function generate_id_hex()
    {
        static $i = 0;
        $i OR $i = mt_rand(1, 0x7FFF);

        return sprintf("%08x%04x",
            /* 4-byte value representing the seconds since the Unix epoch. */
            time() & 0xFFFFFFFF,
            /* 3-byte counter, starting with a random value. */
            $i = $i > 0xFFFE ? 1 : $i + 1
        );
    }
}


