<?php
/**
 * This file has been auto generated
 * Do not change it
 */

namespace Test\Types;

class JsonCollection implements Collection, \JsonSerializable
{
    private $rawData;
    private $keys;
    private $indexes = [];
    private $iterator;

    public function __construct(array $data = [])
    {
        $this->keys = array_keys($data);
        $this->index($data);
        $this->rawData = $data;
        $this->iterator = $this->getIterator();
    }

    public function jsonSerialize()
    {
        return $this->rawData;
    }

    /**
     * @inheritdoc
     */
    public static function fromArray(array $data)
    {
        return new static($data);
    }

    protected function index($data)
    {
    }

    final protected function raw($index)
    {
        if (isset($this->rawData[$index])) {
            return $this->rawData[$index];
        }
        return null;
    }

    final protected function rawSet($data, $index)
    {
        if (is_null($index)) {
            $this->rawData[] = $data;
        } else {
            $this->rawData[$index] = $data;
        }
    }

    /**
     * @param $value
     * @return Collection
     */
    public function add($value) {
        $this->rawSet($value, null);

        return $this;
    }

    public function at($index)
    {
        return $this->map($this->raw($index), $index);
    }

    public function map($data, $index)
    {
        return $data;
    }

    final protected function addToIndex($index, $key, $value)
    {
        $this->indexes[$index][$key] = $value;
    }

    final protected function valueByKey($index, $key)
    {
        return isset($this->indexes[$index][$key]) ? $this->at($this->indexes[$index][$key]) : null;
    }

    /**
     * @return MapIterator
     */
    public function getIterator()
    {
        return new MapIterator($this->rawData, [$this, 'map']);
    }

    /**
     * @inheritDoc
     */
    public function current()
    {
        return $this->iterator->current();
    }

    /**
     * @inheritDoc
     */
    public function next()
    {
        $this->iterator->next();
    }

    /**
     * @inheritDoc
     */
    public function key()
    {
        $this->iterator->key();
    }

    /**
     * @inheritDoc
     */
    public function valid()
    {
        $this->iterator->valid();
    }

    /**
     * @inheritDoc
     */
    public function rewind()
    {
        $this->iterator->rewind();
    }

    /**
     * @inheritdoc
     */
    public function offsetExists($offset)
    {
        return array_key_exists($offset, $this->rawData);
    }

    /**
     * @inheritdoc
     */
    public function offsetGet($offset)
    {
        return $this->at($offset);
    }

    /**
     * @inheritdoc
     */
    public function offsetSet($offset, $value)
    {
        $this->rawSet($value, $offset);
    }

    /**
     * @inheritdoc
     */
    public function offsetUnset($offset)
    {
        unset($this->rawData[$offset]);
    }
}
