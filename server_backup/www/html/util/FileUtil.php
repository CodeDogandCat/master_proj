<?php

class FileUtil
{
    public static function createFolder($path)
    {
        if (!file_exists($path)) {
            createFolder(dirname($path));
            return mkdir($path, 0777);
        }
    }

    public static function get_extension($file)
    {
        return end(explode('.', $file));
    }

    public static function delFile($file)
    {
        return @unlink($file);
    }
}