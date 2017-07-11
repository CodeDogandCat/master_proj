<?php

class Meeting
{
    private $id, $url, $theme, $host_user_id, $is_drawable,
        $is_talkable, $is_add_to_calendar, $start_time, $end_time, $status, $password,$event_id,$meeting_desc;

    /**
     * @return mixed
     */
    public function getEventId()
    {
        return $this->event_id;
    }

    /**
     * @param mixed $event_id
     */
    public function setEventId($event_id)
    {
        $this->event_id = $event_id;
    }

    /**
     * @return mixed
     */
    public function getMeetingDesc()
    {
        return $this->meeting_desc;
    }

    /**
     * @param mixed $meeting_desc
     */
    public function setMeetingDesc($meeting_desc)
    {
        $this->meeting_desc = $meeting_desc;
    }



    /**
     * Meeting constructor.
     * @param $theme
     * @param $host_user_id
     * @param $is_drawable
     * @param $is_talkable
     * @param $is_add_to_calendar
     * @param $start_time
     * @param $end_time
     * @param $status
     */
    public function __construct($theme, $host_user_id, $is_drawable, $is_talkable, $is_add_to_calendar, $start_time, $end_time, $password, $status)
    {
        $this->theme = $theme;
        $this->host_user_id = $host_user_id;
        $this->is_drawable = $is_drawable;
        $this->is_talkable = $is_talkable;
        $this->is_add_to_calendar = $is_add_to_calendar;
        $this->start_time = $start_time;
        $this->end_time = $end_time;
        $this->status = $status;
        $this->password = $password;
    }

    /**
     * @return mixed
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * @param mixed $password
     */
    public function setPassword($password)
    {
        $this->password = $password;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @return mixed
     */
    public function getUrl()
    {
        return $this->url;
    }

    /**
     * @param mixed $url
     */
    public function setUrl($url)
    {
        $this->url = $url;
    }

    /**
     * @return mixed
     */
    public function getTheme()
    {
        return $this->theme;
    }

    /**
     * @param mixed $theme
     */
    public function setTheme($theme)
    {
        $this->theme = $theme;
    }

    /**
     * @return mixed
     */
    public function getHostUserId()
    {
        return $this->host_user_id;
    }

    /**
     * @param mixed $host_user_id
     */
    public function setHostUserId($host_user_id)
    {
        $this->host_user_id = $host_user_id;
    }

    /**
     * @return mixed
     */
    public function getIsDrawable()
    {
        return $this->is_drawable;
    }

    /**
     * @param mixed $is_drawable
     */
    public function setIsDrawable($is_drawable)
    {
        $this->is_drawable = $is_drawable;
    }

    /**
     * @return mixed
     */
    public function getIsTalkable()
    {
        return $this->is_talkable;
    }

    /**
     * @param mixed $is_talkable
     */
    public function setIsTalkable($is_talkable)
    {
        $this->is_talkable = $is_talkable;
    }


    /**
     * @return mixed
     */
    public function getIsAddToCalendar()
    {
        return $this->is_add_to_calendar;
    }

    /**
     * @param mixed $is_add_to_calendar
     */
    public function setIsAddToCalendar($is_add_to_calendar)
    {
        $this->is_add_to_calendar = $is_add_to_calendar;
    }

    /**
     * @return mixed
     */
    public function getStartTime()
    {
        return $this->start_time;
    }

    /**
     * @param mixed $start_time
     */
    public function setStartTime($start_time)
    {
        $this->start_time = $start_time;
    }

    /**
     * @return mixed
     */
    public function getEndTime()
    {
        return $this->end_time;
    }

    /**
     * @param mixed $end_time
     */
    public function setEndTime($end_time)
    {
        $this->end_time = $end_time;
    }

    /**
     * @return mixed
     */
    public function getStatus()
    {
        return $this->status;
    }

    /**
     * @param mixed $status
     */
    public function setStatus($status)
    {
        $this->status = $status;
    }

}