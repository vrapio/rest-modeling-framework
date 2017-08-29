<?php
/**
 * This file has been auto generated
 * Do not change it
 */

namespace Test\Types;

class JsonObjectModel implements JsonObject
{
    private $rawData;

    public function __construct(array $data = [])
    {
        $this->rawData = $data;
    }

    final protected function raw($field)
    {
        if (isset($this->rawData[$field])) {
            return $this->rawData[$field];
        }
        return null;
    }


    public function isPresent($field)
    {
        return isset($this->rawData[$field]) || isset($this->$field);
    }

    public function jsonSerialize()
    {
        return $this->toArray();
    }

    /**
     * @inheritdoc
     */
    public static function fromArray(array $data)
    {
        return new static($data);
    }

    /**
     * @inheritdoc
     */
    private function toArray()
    {
        $data = array_filter(
            get_object_vars($this),
            function ($value, $key) {
                if ($key == 'rawData') {
                    return false;
                }
                return !is_null($value);
            },
            ARRAY_FILTER_USE_BOTH
        );
        $data = array_merge($this->rawData, $data);
        return $data;
    }
}
