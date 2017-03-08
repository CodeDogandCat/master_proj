<?php

class UrlUtil
{
    //解析URL参数
    public static function parseUrlParam($query)
    {
        $queryArr = explode('&', $query);
        $params = array();
        if ($queryArr[0] !== '') {
            foreach ($queryArr as $param) {
                list($name, $value) = explode('=', $param);
                $params[urldecode($name)] = urldecode($value);
            }
        }
        return $params;
    }

    //设置URL参数数组
    public static function setUrlParams($cparams, $url = '')
    {
        $parse_url = $url === '' ? parse_url(UrlUtil::request_url()) : parse_url($url);
        $query = isset($parse_url['query']) ? $parse_url['query'] : '';
        $params = UrlUtil::parseUrlParam($query);
        foreach ($cparams as $key => $value) {
            $params[$key] = $value;
        }
        return $parse_url['path'] . '?' . http_build_query($params);
    }

    //获取URL参数
    public static function getUrlParam($cparam, $url = '')
    {
        $parse_url = $url === '' ? parse_url(UrlUtil::request_url()) : parse_url($url);
        $query = isset($parse_url['query']) ? $parse_url['query'] : '';
        $params = UrlUtil::parseUrlParam($query);
        return isset($params[$cparam]) ? $params[$cparam] : '';
    }

    //获取URL
    public static function request_url()
    {
        if (isset($_SERVER['REQUEST_URI'])) {
            $uri = $_SERVER['REQUEST_URI'];
        } else {
            if (isset($_SERVER['argv'])) {
                $uri = $_SERVER['PHP_SELF'] . '?' . $_SERVER['argv'][0];
            } else {
                $uri = $_SERVER['PHP_SELF'] . '?' . $_SERVER['QUERY_STRING'];
            }
        }
        return $uri;
    }
}